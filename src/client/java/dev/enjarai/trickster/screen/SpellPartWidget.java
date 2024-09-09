package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.revision.Revision;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.revision.Revisions;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class SpellPartWidget extends AbstractParentElement implements Drawable, Selectable {
    public static final double PRECISION_OFFSET = Math.pow(2, 50);

    private SpellPart spellPart;

//    private List<SpellPartWidget> partWidgets;

    public double x;
    public double y;
    public double size;

    private double amountDragged;
    private boolean isMutable = true;

    @Nullable
    private SpellPart toBeReplaced;

    private Hamt<Pattern, SpellPart> macros;
    private final RevisionContext revisionContext;
    private SpellPart drawingPart;
    private Fragment oldGlyph;
    private List<Byte> drawingPattern;

    public final SpellCircleRenderer renderer;

    public SpellPartWidget(SpellPart spellPart, double x, double y, double size, Hamt<Pattern, SpellPart> macros, RevisionContext revisionContext) {
        this.spellPart = spellPart;
        this.x = toScaledSpace(x);
        this.y = toScaledSpace(y);
        this.size = toScaledSpace(size);
        this.macros = macros;
        this.revisionContext = revisionContext;
        this.renderer = new SpellCircleRenderer(() -> this.drawingPart, () -> this.drawingPattern, PRECISION_OFFSET);
    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    public void setSpell(SpellPart spellPart) {
        this.spellPart = spellPart;
    }

    public void setMacros(Hamt<Pattern, SpellPart> macros) {
        this.macros = macros;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isMutable) {
            this.renderer.setMousePosition(mouseX, mouseY);
        }
        this.renderer.renderPart(
                context.getMatrices(), context.getVertexConsumers(), spellPart,
                x, y, size, 0, delta,
                size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 3) - 0.2, 0, 1),
                new Vec3d(-1, 0, 0)
        );
        context.draw();
    }

    public static boolean isCircleClickable(double size) {
        return size >= 16 && size <= 256;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public void setMutable(boolean mutable) {
        isMutable = mutable;
        if (!mutable) {
            this.renderer.setMousePosition(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true; // TODO make more granular?
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        double scaledAmount = toScaledSpace(verticalAmount);
        size += scaledAmount * toLocalSpace(size) / 10;
        x += scaledAmount * (toLocalSpace(x) - mouseX) / 10;
        y += scaledAmount * (toLocalSpace(y) - mouseY) / 10;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if (!isDrawing()) {
            x += toScaledSpace(deltaX);
            y += toScaledSpace(deltaY);

            amountDragged += Math.abs(deltaX) + Math.abs(deltaY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMutable || isDrawing()) {
            if (Trickster.CONFIG.dragDrawing() && button == 0 && !isDrawing()) {
                if (propagateMouseEvent(spellPart, x, y, size, 0, toScaledSpace(mouseX), toScaledSpace(mouseY),
                        (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY))) {
                    return true;
                }
            } else {
                // We need to return true on the mouse down event to make sure the screen knows if we're on a clickable node
                if (propagateMouseEvent(spellPart, x, y, size, 0, toScaledSpace(mouseX), toScaledSpace(mouseY),
                        (part, x, y, size) -> true)) {
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isMutable || isDrawing()) {
            var dragged = amountDragged;
            amountDragged = 0;

            if (dragged > 5) {
                return false;
            }

            if (!Trickster.CONFIG.dragDrawing() && button == 0 && !isDrawing()) {
                if (propagateMouseEvent(spellPart, x, y, size, 0, toScaledSpace(mouseX), toScaledSpace(mouseY),
                        (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY))) {
                    return true;
                }
            }

            if (drawingPart != null) {
                stopDrawing();
                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isDrawing()) {
            propagateMouseEvent(spellPart, x, y, size, 0, toScaledSpace(mouseX), toScaledSpace(mouseY),
                    (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY));
        }

        super.mouseMoved(mouseX, mouseY);
    }

    protected boolean selectPattern(SpellPart part, float x, float y, float size, double mouseX, double mouseY) {
        if (drawingPart != null && drawingPart != part) {
            // Cancel early if we're already drawing in another part
            return false;
        }

        var patternSize = size / PATTERN_TO_PART_RATIO;
        var pixelSize = patternSize / PART_PIXEL_RADIUS;

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x, y, i, patternSize);

            if (isInsideHitbox(pos, pixelSize, mouseX, mouseY)) {
                if (drawingPart == null) {
                    drawingPart = part;
                    oldGlyph = part.glyph;
                    part.glyph = new PatternGlyph(List.of());
                    drawingPattern = new ArrayList<>();
                }

                if (drawingPattern.size() >= 2 && drawingPattern.get(drawingPattern.size() - 2) == (byte) i) {
                    drawingPattern.removeLast();
                    MinecraftClient.getInstance().player.playSoundToPlayer(
                            ModSounds.DRAW, SoundCategory.MASTER,
                            1f, ModSounds.randomPitch(0.6f, 0.2f)
                    );
                } else if (drawingPattern.isEmpty() ||
                        (drawingPattern.getLast() != (byte) i && !hasOverlappingLines(drawingPattern, drawingPattern.getLast(), (byte) i))) {
                    drawingPattern.add((byte) i);

                    //add middle point to path if connecting opposite corners
                    if (drawingPattern.size() > 1 && drawingPattern.get(drawingPattern.size() - 2) == (byte) (8 - i))
                        drawingPattern.add(drawingPattern.size() - 1, (byte) 4);

                    // TODO click sound?
                    MinecraftClient.getInstance().player.playSoundToPlayer(
                            ModSounds.DRAW, SoundCategory.MASTER,
                            1f, ModSounds.randomPitch(1f, 0.2f)
                    );
                }

                return true;
            }
        }

        return false;
    }

    protected void stopDrawing() {
        var compiled = Pattern.from(drawingPattern);
        var patternSize = drawingPattern.size();
        var rev = Revisions.lookup(compiled);
        var tryReset = true;

        if (compiled.equals(Revisions.EXECUTE_OFF_HAND.pattern())) {
            toBeReplaced = drawingPart; //TODO: allow handling this in a more generic way?
            Revisions.EXECUTE_OFF_HAND.apply(revisionContext, spellPart, drawingPart);
        } else if (rev.isPresent()) {
            spellPart = rev.get().apply(revisionContext, spellPart, drawingPart);
        } else if (macros.get(compiled).isPresent()) {
            var spell = macros.get(compiled).get();
            var part = drawingPart.deepClone();
            part.glyph = oldGlyph;
            revisionContext.updateSpellWithSpell(part, spell);
            return;
        } else {
            if (patternSize >= 2) {
                drawingPart.glyph = new PatternGlyph(compiled);
            }

            tryReset = false;
        }

        if (tryReset && drawingPart.glyph instanceof PatternGlyph patternGlyph && patternGlyph.pattern().isEmpty()) {
            drawingPart.glyph = oldGlyph;
        }

        drawingPart = null;
        drawingPattern = null;

        revisionContext.updateSpell(spellPart);

        MinecraftClient.getInstance().player.playSoundToPlayer(
                ModSounds.COMPLETE, SoundCategory.MASTER,
                1f, patternSize > 1 ? 1f : 0.6f
        );
    }

    public void replaceCallback(Fragment fragment) {
        if (toBeReplaced != null) {
            toBeReplaced.glyph = fragment;
            toBeReplaced = null;
            revisionContext.updateSpell(spellPart);
        }
    }

    public void updateDrawingPartCallback(Optional<SpellPart> spell) {
        if (spell.isPresent()) {
            drawingPart.glyph = spell.get().glyph;
            drawingPart.subParts = spell.get().subParts;
        } else {
            drawingPart.glyph = oldGlyph;
        }

        var patternSize = drawingPattern.size();
        drawingPart = null;
        drawingPattern = null;

        revisionContext.updateSpell(spellPart);

        MinecraftClient.getInstance().player.playSoundToPlayer(
                ModSounds.COMPLETE, SoundCategory.MASTER,
                1f, patternSize > 1 ? 1f : 0.6f
        );
    }

    public boolean isDrawing() {
        return drawingPart != null;
    }

    protected static boolean hasOverlappingLines(List<Byte> pattern, byte p1, byte p2) {
        Byte first = null;

        for (Byte second : pattern) {
            if (first != null && (first == p1 && second == p2 || first == p2 && second == p1)) {
                return true;
            }

            first = second;
        }

        return false;
    }

    protected boolean propagateMouseEvent(SpellPart part, double x, double y, double size, float startingAngle, double mouseX, double mouseY, MouseEventHandler callback) {
        var closest = part;
        var closestAngle = startingAngle;
        var closestX = x;
        var closestY = y;
        var closestSize = size;

        // These two dont need to be updated for the actual closest
        var initialDiffX = x - mouseX;
        var initialDiffY = y - mouseY;

        var centerAvailable = (isCircleClickable(toLocalSpace(size)) && (drawingPart == null || drawingPart == part)) || part.glyph instanceof SpellPart;
        var closestDistanceSquared = centerAvailable ? initialDiffX * initialDiffX + initialDiffY * initialDiffY : Double.MAX_VALUE;

        int partCount = part.getSubParts().size();
        // We dont change this, its the same for all subcircles
        var nextSize = Math.min(size / 2, size / (float) ((partCount + 1) / 2));
        int i = 0;
        for (var child : part.getSubParts()) {
            var angle = startingAngle + (2 * Math.PI) / partCount * i - (Math.PI / 2);

            var nextX = x + (size * Math.cos(angle));
            var nextY = y + (size * Math.sin(angle));
            var diffX = nextX - mouseX;
            var diffY = nextY - mouseY;
            var distanceSquared = diffX * diffX + diffY * diffY;

            if (distanceSquared < closestDistanceSquared) {
                closest = child;
                closestAngle = (float) angle;
                closestX = (float) nextX;
                closestY = (float) nextY;
                closestSize = nextSize;
                closestDistanceSquared = distanceSquared;
            }

            i++;
        }

        if (Math.sqrt(closestDistanceSquared) <= size && toLocalSpace(size) >= 16) {
            if (closest == part) {
                // Special handling for part glyphs, because of course
                // This makes it impossible to interact with direct parents of part glyphs, but thats not an issue
                if (closest.glyph instanceof SpellPart centerPart) {
                    closest = centerPart;
                    closestSize /= 3;
                } else {
                    return callback.handle(closest, toLocalSpace(closestX), toLocalSpace(closestY), toLocalSpace(closestSize));
                }
            }

            return propagateMouseEvent(closest, closestX, closestY, closestSize, closestAngle, mouseX, mouseY, callback);
        }

        return false;
    }

    private static float toLocalSpace(double value) {
        return (float) (value * PRECISION_OFFSET);
    }

    private static double toScaledSpace(double value) {
        return (float) (value / PRECISION_OFFSET);
    }

    interface MouseEventHandler {
        boolean handle(SpellPart part, float x, float y, float size);
    }

//    @Override
//    public void setX(int x) {
//        this.x = x + size;
//    }
//
//    @Override
//    public void setY(int y) {
//        this.y = y + size;
//    }
//
//    @Override
//    public int getX() {
//        return (int) (x - size);
//    }
//
//    @Override
//    public int getY() {
//        return (int) (y - size);
//    }
//
//    @Override
//    public int getWidth() {
//        return (int) size * 2;
//    }
//
//    @Override
//    public int getHeight() {
//        return (int) size * 2;
//    }
//
//    @Override
//    public void forEachChild(Consumer<ClickableWidget> consumer) {
//
//    }
}

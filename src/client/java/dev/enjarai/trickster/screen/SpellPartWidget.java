package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class SpellPartWidget extends AbstractParentElement implements Drawable, Selectable {
    public static final Pattern CREATE_SUBCIRCLE_GLYPH = Pattern.of(0, 4, 8, 7);
    public static final Pattern CREATE_GLYPH_CIRCLE_GLYPH = Pattern.of(0, 4, 8, 5);
    public static final Pattern CREATE_PARENT_GLYPH = Pattern.of(3, 0, 4, 8);
    public static final Pattern CREATE_PARENT_GLYPH_GLYPH = Pattern.of(1, 0, 4, 8);
    public static final Pattern EXPAND_TO_OUTER_CIRCLE_GLYPH = Pattern.of(1, 2, 4, 6);
    public static final Pattern DELETE_CIRCLE_GLYPH = Pattern.of(0, 4, 8);
    public static final Pattern DELETE_BRANCH_GLYPH = Pattern.of(0, 4, 8, 5, 2, 1, 0, 3, 6, 7, 8);
    public static final Pattern COPY_OFFHAND_LITERAL = Pattern.of(4, 0, 1, 4, 2, 1);
    public static final Pattern COPY_OFFHAND_LITERAL_INNER = Pattern.of(1, 2, 4, 1, 0, 4, 7);
    public static final Pattern COPY_OFFHAND_EXECUTE = Pattern.of(4, 3, 0, 4, 5, 2, 4, 1);
    public static final Pattern WRITE_OFFHAND_ADDRESS = Pattern.of(1, 0, 4, 8, 7, 6, 4, 2, 1, 4);


    private SpellPart spellPart;
//    private List<SpellPartWidget> partWidgets;

    public BigDecimal x;
    public BigDecimal y;
    public BigDecimal size;
    private double amountDragged;

    private boolean isMutable = true;

    private Consumer<SpellPart> updateListener;
    private Consumer<SpellPart> otherHandSpellUpdateListener;
    private Supplier<SpellPart> otherHandSpellSupplier;
    @Nullable
    private SpellPart toBeReplaced;
    private Runnable initializeReplace;

    private SpellPart drawingPart;
    private Fragment oldGlyph;
    private List<Byte> drawingPattern;

    private final SpellCircleRenderer renderer;

    public SpellPartWidget(SpellPart spellPart, double x, double y, double size, Consumer<SpellPart> spellUpdateListener, Consumer<SpellPart> otherHandSpellUpdateListener, Supplier<SpellPart> otherHandSpellSupplier, Runnable initializeReplace) {
        this.spellPart = spellPart;
        this.x = new BigDecimal(x);
        this.y = new BigDecimal(y);
        this.size = new BigDecimal(size);
        this.updateListener = spellUpdateListener;
        this.otherHandSpellUpdateListener = otherHandSpellUpdateListener;
        this.otherHandSpellSupplier = otherHandSpellSupplier;
        this.initializeReplace = initializeReplace;
        this.renderer = new SpellCircleRenderer(() -> this.drawingPart, () -> this.drawingPattern);
    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    public void setSpell(SpellPart spellPart) {
        this.spellPart = spellPart;
//        partWidgets.clear();
//        spellPart.
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isMutable) {
            this.renderer.setMousePosition(mouseX, mouseY);
        }
        this.renderer.renderPart(
                context.getMatrices(), context.getVertexConsumers(), spellPart,
                // We're casting the big decimals down to doubles here, which means we're definitely not implementing
                // infinite scrollability yet. We weren't using doubles in the renderer before though, so having that
                // bit of extra precision might already make a big difference. If we actually do run into more issues,
                // I *can* convert the renderer to big decimals as well, but that would type in an insane amount of
                // object allocations every frame, which could very well impact performance significantly.
                x.doubleValue(), y.doubleValue(), size.doubleValue(), 0, delta,
                size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 2) - 0.2, 0, 1),
                new Vec3d(0, 0, -1)
        );
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
            this.renderer.setMousePosition(Double.MIN_VALUE, Double.MIN_VALUE);
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

        var ten = new BigDecimal(10);
        var vertical = new BigDecimal(verticalAmount);

        size = size.add(vertical.multiply(size).divide(ten, RoundingMode.DOWN)); // verticalAmount * size / 10
        x = x.add(vertical.multiply(x.subtract(new BigDecimal(mouseX))).divide(ten, RoundingMode.DOWN)); // verticalAmount * (x - mouseX) / 10;
        y = y.add(vertical.multiply(y.subtract(new BigDecimal(mouseY))).divide(ten, RoundingMode.DOWN)); // verticalAmount * (y - mouseY) / 10;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if (!isDrawing()) {
            x = x.add(new BigDecimal(deltaX));
            y = y.add(new BigDecimal(deltaY));

            amountDragged += Math.abs(deltaX) + Math.abs(deltaY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMutable || isDrawing()) {
            if (Trickster.CONFIG.dragDrawing() && button == 0 && !isDrawing()) {
                if (propagateMouseEvent(spellPart, x.doubleValue(), y.doubleValue(), size.doubleValue(), 0, mouseX, mouseY,
                        (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY))) {
                    return true;
                }
            } else {
                // We need to return true on the mouse down event to make sure the screen knows if we're on a clickable node
                if (propagateMouseEvent(spellPart, x.doubleValue(), y.doubleValue(), size.doubleValue(), 0, mouseX, mouseY,
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
                if (propagateMouseEvent(spellPart, x.doubleValue(), y.doubleValue(), size.doubleValue(), 0, mouseX, mouseY,
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
            propagateMouseEvent(spellPart, x.doubleValue(), y.doubleValue(), size.doubleValue(), 0, mouseX, mouseY,
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
        var tryReset = true;

        if (compiled.equals(CREATE_SUBCIRCLE_GLYPH)) {
            drawingPart.subParts.add(new SpellPart());
        } else if (compiled.equals(CREATE_GLYPH_CIRCLE_GLYPH)) {
            drawingPart.glyph = new SpellPart();
        } else if (compiled.equals(CREATE_PARENT_GLYPH)) {
            var newPart = new SpellPart();
            newPart.subParts.add(drawingPart);
            if (drawingPart == spellPart) {
                spellPart = newPart;
            } else {
                drawingPart.setSubPartInTree(Optional.of(newPart), spellPart, false);
            }
        } else if (compiled.equals(CREATE_PARENT_GLYPH_GLYPH)) {
            var newPart = new SpellPart();
            newPart.glyph = drawingPart;
            if (drawingPart == spellPart) {
                spellPart = newPart;
            } else {
                drawingPart.setSubPartInTree(Optional.of(newPart), spellPart, false);
            }
        } else if (compiled.equals(EXPAND_TO_OUTER_CIRCLE_GLYPH)) {
            if (drawingPart != spellPart) {
                if (spellPart.glyph == drawingPart) {
                    spellPart = drawingPart;
                } else {
                    drawingPart.setSubPartInTree(Optional.of(drawingPart), spellPart, true);
                }
            }
        } else if (compiled.equals(DELETE_CIRCLE_GLYPH)) {
            var firstSubpart = drawingPart.getSubParts().stream().findFirst();
            if (drawingPart == spellPart) {
                spellPart = firstSubpart.orElse(new SpellPart());
            } else {
                drawingPart.setSubPartInTree(firstSubpart, spellPart, false);
            }
        } else if (compiled.equals(DELETE_BRANCH_GLYPH)) {
            if (drawingPart == spellPart) {
                spellPart = new SpellPart();
            } else {
                drawingPart.setSubPartInTree(Optional.empty(), spellPart, false);
            }
        } else if (compiled.equals(COPY_OFFHAND_LITERAL)) {
            if (drawingPart == spellPart) {
                spellPart = otherHandSpellSupplier.get().deepClone();
            } else {
                drawingPart.setSubPartInTree(Optional.of(otherHandSpellSupplier.get().deepClone()), spellPart, false);
            }
        } else if (compiled.equals(COPY_OFFHAND_LITERAL_INNER)) {
            drawingPart.glyph = otherHandSpellSupplier.get().deepClone();
        } else if (compiled.equals(COPY_OFFHAND_EXECUTE)) {
            toBeReplaced = drawingPart;
            initializeReplace.run();
        } else if (compiled.equals(WRITE_OFFHAND_ADDRESS)) {
            var address = getAddress(spellPart, drawingPart);
            if (address.isPresent()) {
                var addressFragment = new ListFragment(address.get().stream().map(num -> (Fragment) new NumberFragment(num)).toList());
                otherHandSpellUpdateListener.accept(new SpellPart(addressFragment, List.of()));
            }
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

        updateListener.accept(spellPart);

        MinecraftClient.getInstance().player.playSoundToPlayer(
                ModSounds.COMPLETE, SoundCategory.MASTER,
                1f, patternSize > 1 ? 1f : 0.6f
        );
    }

    public void replaceCallback(Fragment fragment) {
        if (toBeReplaced != null) {
            toBeReplaced.glyph = fragment;
            toBeReplaced = null;
            updateListener.accept(spellPart);
        }
    }

    public boolean isDrawing() {
        return drawingPart != null;
    }

    protected Optional<List<Integer>> getAddress(SpellPart node, SpellPart target) {
        var address = new LinkedList<Integer>();
        var found = getAddress(node, target, address, new LinkedList<>());
        if (found) {
            return Optional.of(address);
        } else {
            return Optional.empty();
        }
    }

    protected boolean getAddress(SpellPart node, SpellPart target, List<Integer> address, List<SpellPart> glyphSpells) {
        if (node == target) {
            return true;
        }
        if (node.glyph instanceof SpellPart glyph) {
            glyphSpells.add(glyph);
        }

        var subParts = node.subParts;

        for (int i = 0; i < subParts.size(); i++) {
            address.add(i);
            var found = getAddress(subParts.get(i), target, address, glyphSpells);
            if (found) return true;
            address.removeLast();
        }
        if (address.isEmpty()) {
            for (var glyph : glyphSpells) {
                var found = getAddress(glyph, target, address, new LinkedList<>());
                if (found) return true;
            }
        }

        return false;
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

    protected static boolean propagateMouseEvent(SpellPart part, double x, double y, double size, float startingAngle, double mouseX, double mouseY, MouseEventHandler callback) {
        var closest = part;
        var closestAngle = startingAngle;
        var closestX = x;
        var closestY = y;
        var closestSize = size;

        // These two dont need to be updated for the actual closest
        var initialDiffX = x - mouseX;
        var initialDiffY = y - mouseY;

        var centerAvailable = isCircleClickable(size) || part.glyph instanceof SpellPart;
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

        if (Math.sqrt(closestDistanceSquared) <= size && size >= 16) {
            if (closest == part) {
                // Special handling for part glyphs, because of course
                // This makes it impossible to interact with direct parents of part glyphs, but thats not an issue
                if (closest.glyph instanceof SpellPart centerPart) {
                    closest = centerPart;
                    closestSize /= 3;
                } else {
                    return callback.handle(closest, (float) closestX, (float) closestY, (float) closestSize);
                }
            }

            return propagateMouseEvent(closest, closestX, closestY, closestSize, closestAngle, mouseX, mouseY, callback);
        }

        return false;
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

package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.revision.Revisions;
import dev.enjarai.trickster.spell.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class SpellPartWidget extends AbstractParentElement implements Drawable, Selectable {
    public static final double PRECISION_OFFSET = Math.pow(2, 50);

    static final Byte[] RING_ORDER = {
            (byte) 0, (byte) 1, (byte) 2, (byte) 5, (byte) 8, (byte) 7, (byte) 6, (byte) 3
    };
    static final Byte[] RING_INDICES = {
            (byte) 0, (byte) 1, (byte) 2, (byte) 7, (byte) 0, (byte) 3, (byte) 6, (byte) 5, (byte) 4
    };

    private SpellPart rootSpellPart;
    private SpellPart spellPart;
    private final Stack<SpellPart> parents = new Stack<>();
    private final Stack<Double> angleOffsets = new Stack<>();

    public double x;
    public double y;
    public double size;

    private double amountDragged;
    private boolean isMutable = true;

    @Nullable
    private SpellPart toBeReplaced;

    private final Vector2d originalPosition;
    private final RevisionContext revisionContext;
    private SpellPart drawingPart;
    private Fragment oldGlyph;
    private List<Byte> drawingPattern;

    public final SpellCircleRenderer renderer;

    public SpellPartWidget(SpellPart spellPart, double x, double y, double size, RevisionContext revisionContext, boolean animated) {
        this.rootSpellPart = spellPart;
        this.spellPart = spellPart;
        this.originalPosition = new Vector2d(toScaledSpace(x), toScaledSpace(y));
        this.x = toScaledSpace(x);
        this.y = toScaledSpace(y);
        this.size = toScaledSpace(size);
        this.revisionContext = revisionContext;
        this.renderer = new SpellCircleRenderer(() -> this.drawingPart, () -> this.drawingPattern, PRECISION_OFFSET, animated);
        this.angleOffsets.push(0d);
    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    public void setSpell(SpellPart spellPart) {
        var newParents = new Stack<SpellPart>();
        var newAngleOffsets = new Stack<Double>();
        newParents.push(spellPart);

        var currentParents = new ArrayList<>(this.parents);
        var currentAngleOffsets = new ArrayList<>(this.angleOffsets);
        newAngleOffsets.push(currentAngleOffsets.removeFirst());

        for (int i = currentParents.size() - 1; i >= 0; i--) {
            var currentParent = currentParents.removeFirst();
            var currentChild = !currentParents.isEmpty() ? currentParents.getFirst() : this.spellPart;

            if (currentParent.glyph instanceof SpellPart spellGlyph && spellGlyph == currentChild) {
                if (newParents.peek().glyph instanceof SpellPart newSpellGlyph)
                    newParents.push(newSpellGlyph);
                else break;
            } else {
                var failed = true;
                int i2 = 0;

                for (var child : currentParent.subParts) {
                    if (child == currentChild) {
                        if (newParents.peek().subParts.size() > i2) {
                            newParents.push(newParents.peek().subParts.get(i2));
                            failed = false;
                        }

                        break;
                    }

                    i2++;
                }

                if (failed) {
                    this.x = originalPosition.x;
                    this.y = originalPosition.y;
                    break;
                }
            }

            newAngleOffsets.push(currentAngleOffsets.removeFirst());
        }

        this.rootSpellPart = spellPart;
        this.spellPart = newParents.pop();
        this.parents.clear();
        this.angleOffsets.clear();
        this.parents.addAll(new ArrayList<>(newParents));
        this.angleOffsets.addAll(new ArrayList<>(newAngleOffsets));
    }

    public ScrollAndQuillScreen.PositionMemory save() {
        return new ScrollAndQuillScreen.PositionMemory(rootSpellPart.hashCode(), x, y, size, rootSpellPart, spellPart, new ArrayList<>(parents), new ArrayList<>(angleOffsets));
    }

    public void load(ScrollAndQuillScreen.PositionMemory memory) {
        this.x = memory.x();
        this.y = memory.y();
        this.size = memory.size();
        this.rootSpellPart = memory.rootSpellPart();
        this.spellPart = memory.spellPart();
        this.parents.clear();
        this.angleOffsets.clear();
        this.parents.addAll(memory.parents());
        this.angleOffsets.addAll(memory.angleOffsets());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isMutable) {
            this.renderer.setMousePosition(mouseX, mouseY);
        }

        //        context.getMatrices().push();
        //        context.getMatrices().scale((float) PRECISION_OFFSET, (float) PRECISION_OFFSET, (float) PRECISION_OFFSET);

        this.renderer.renderPart(
                context.getMatrices(), context.getVertexConsumers(), spellPart,
                x, y, size, angleOffsets.peek(), delta,
                size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 3) - 0.2, 0, 1),
                new Vec3d(-1, 0, 0)
        );
        context.draw();

        //        context.getMatrices().pop();
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

        size += verticalAmount * size / 10;
        x += verticalAmount * (x - toScaledSpace(mouseX)) / 10;
        y += verticalAmount * (y - toScaledSpace(mouseY)) / 10;

        if (toLocalSpace(size) > 600) {
            pushNewRoot(toScaledSpace(mouseX), toScaledSpace(mouseY));
        } else if (toLocalSpace(size) < 300 && !parents.empty()) {
            popOldRoot();
        }

        return true;
    }

    private void popOldRoot() {
        var result = parents.pop();
        angleOffsets.pop();

        int partCount = result.subParts.size();
        var parentSize = size * 3;
        int i = 0;

        if (!(result.glyph instanceof SpellPart inner && inner == spellPart)) {
            parentSize = Math.max(size * 2, size * (double) ((partCount + 1) / 2));

            for (var child : result.subParts) {
                if (child == spellPart) {
                    var angle = angleOffsets.peek() + (2 * Math.PI) / partCount * i - (Math.PI / 2);
                    x -= parentSize * Math.cos(angle);
                    y -= parentSize * Math.sin(angle);
                    break;
                }

                i++;
            }
        }

        size = parentSize;
        spellPart = result;
    }

    private void pushNewRoot(double mouseX, double mouseY) {
        var closest = spellPart;
        var closestAngle = angleOffsets.peek();
        var closestDiffX = 0d;
        var closestDiffY = 0d;
        var closestDistanceSquared = Double.MAX_VALUE;
        var closestSize = size / 3;

        int partCount = spellPart.subParts.size();
        var nextSize = Math.min(size / 2, size / (double) ((partCount + 1) / 2));
        int i = 0;

        if (spellPart.glyph instanceof SpellPart inner) {
            var mDiffX = x - mouseX;
            var mDiffY = y - mouseY;
            var distanceSquared = mDiffX * mDiffX + mDiffY * mDiffY;
            closest = inner;
            closestDistanceSquared = distanceSquared;
        }

        for (var child : spellPart.subParts) {
            var angle = angleOffsets.peek() + (2 * Math.PI) / partCount * i - (Math.PI / 2);
            var nextX = x + (size * Math.cos(angle));
            var nextY = y + (size * Math.sin(angle));
            var diffX = nextX - x;
            var diffY = nextY - y;
            var mDiffX = nextX - mouseX;
            var mDiffY = nextY - mouseY;
            var distanceSquared = mDiffX * mDiffX + mDiffY * mDiffY;

            if (distanceSquared < closestDistanceSquared) {
                closest = child;
                closestAngle = angle;
                closestDiffX = diffX;
                closestDiffY = diffY;
                closestDistanceSquared = distanceSquared;
                closestSize = nextSize;
            }

            i++;
        }

        this.parents.push(spellPart);
        this.angleOffsets.push(closestAngle);
        this.spellPart = closest;
        this.size = closestSize;
        this.x += closestDiffX;
        this.y += closestDiffY;
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
                if (
                    propagateMouseEvent(
                            spellPart, x, y, size, angleOffsets.peek(), toScaledSpace(mouseX), toScaledSpace(mouseY),
                            (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY)
                    )
                ) {
                    return true;
                }
            } else {
                // We need to return true on the mouse down event to make sure the screen knows if we're on a clickable node
                if (
                    propagateMouseEvent(
                            spellPart, x, y, size, angleOffsets.peek(), toScaledSpace(mouseX), toScaledSpace(mouseY),
                            (part, x, y, size) -> true
                    )
                ) {
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
                if (
                    propagateMouseEvent(
                            spellPart, x, y, size, angleOffsets.peek(), toScaledSpace(mouseX), toScaledSpace(mouseY),
                            (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY)
                    )
                ) {
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
            propagateMouseEvent(
                    spellPart, x, y, size, angleOffsets.peek(), toScaledSpace(mouseX), toScaledSpace(mouseY),
                    (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY)
            );
        }

        super.mouseMoved(mouseX, mouseY);
    }

    private static boolean areAdjacent(byte a, byte b) {
        if (a == 4 || b == 4) {
            return false;
        } else {
            var i = RING_INDICES[a];
            return b == RING_ORDER[(i + 1) % 8] ||
                    b == RING_ORDER[i == 0 ? 7 : (i - 1) % 8];
        }
    }

    private boolean hasLine(byte a, byte b) {
        for (int i = 0; i < drawingPattern.size(); i++) {
            if (i < drawingPattern.size() - 1) {
                var prev = drawingPattern.get(i);
                var next = drawingPattern.get(i + 1);
                if ((prev == a && next == b) || (prev == b && next == a)) {
                    return true;
                }
            }
        }
        return false;
    }

    // generates all possible moves from the current drawingPattern
    // assigns the target dot with the resulting next drawingPattern
    private HashMap<Byte, List<Byte>> possibleMoves() {
        var moves = new HashMap<Byte, List<Byte>>();
        if (drawingPattern.isEmpty()) {
            for (byte i = 0; i < 9; i++) {
                var move = new ArrayList<Byte>();
                move.add(i);
                moves.put(i, move);
            }
        } else {
            var last = drawingPattern.getLast();
            for (byte i = 0; i < 9; i++) {
                if (i == last) {
                    continue;
                }

                if (!hasLine(i, last)) {
                    var move = new ArrayList<Byte>(drawingPattern);
                    // resolve the middle dot if we are going across
                    if (i == 8 - last) {
                        if (hasLine(i, (byte) 4) || hasLine(last, (byte) 4)) {
                            // we are already connected to the middle dot
                            // going across is impossible
                            continue;
                        } else {
                            move.add((byte) 4);
                        }
                    }
                    move.add(i);
                    moves.put(i, move);
                } else if (drawingPattern.size() >= 2 && drawingPattern.get(drawingPattern.size() - 2) == i) {
                    var move = new ArrayList<Byte>(drawingPattern);
                    move.removeLast();
                    moves.put(i, move);
                }
            }
        }
        return moves;
    }

    @SuppressWarnings("resource")
    protected boolean selectPattern(SpellPart part, float x, float y, float size, double mouseX, double mouseY) {
        if (drawingPart != null && drawingPart != part) {
            // Cancel early if we're already drawing in another part
            return false;
        }

        var patternSize = size / PATTERN_TO_PART_RATIO;
        var pixelSize = patternSize / PART_PIXEL_RADIUS;

        if (drawingPattern == null) {
            drawingPattern = new ArrayList<>();
        }

        var moves = possibleMoves();

        for (byte i = 0; i < 9; i++) {
            // if we are checking the closest neighboring dots on the ring
            // translate the dots outward a bit by enlarging the radius
            // this will make it easier to connect lines to the next neighbors on the ring
            var _patternSize = patternSize;
            if (!drawingPattern.isEmpty()) {
                var last = drawingPattern.get(drawingPattern.size() - 1);
                if (areAdjacent(last, i)) {
                    _patternSize += pixelSize * Trickster.CONFIG.adjacentPixelCollisionOffset() * 3;
                }
            }

            var pos = getPatternDotPosition(x, y, i, _patternSize);

            if (isInsideHitbox(pos, pixelSize, mouseX, mouseY)) {
                if (drawingPart == null) {
                    drawingPart = part;
                    oldGlyph = part.glyph;
                    part.glyph = new PatternGlyph(List.of());
                }
                if (moves.get(i) != null) {
                    boolean removing = drawingPattern.size() > moves.get(i).size();
                    drawingPattern = moves.get(i);
                    // TODO click sound?
                    MinecraftClient.getInstance().player.playSoundToPlayer(
                            ModSounds.DRAW, SoundCategory.MASTER,
                            1f, ModSounds.randomPitch(removing ? 0.6f : 1.0f, 0.2f)
                    );
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("resource")
    protected void stopDrawing() {
        var compiled = Pattern.from(drawingPattern);
        var patternSize = drawingPattern.size();
        var rev = Revisions.lookup(compiled);

        drawingPart.glyph = oldGlyph;

        if (compiled.equals(Revisions.EXECUTE_OFF_HAND.pattern())) {
            toBeReplaced = drawingPart; //TODO: allow handling this in a more generic way?
            Revisions.EXECUTE_OFF_HAND.apply(revisionContext, spellPart, drawingPart);
        } else if (rev.isPresent()) {
            var result = rev.get().apply(revisionContext, spellPart, drawingPart);

            if (result != spellPart) {
                if (!parents.isEmpty()) {
                    var parent = parents.peek();

                    for (int i = 0; i < parent.subParts.size(); i++) {
                        if (parent.subParts.get(i) == spellPart) {
                            parent.subParts.set(i, result);
                        }
                    }
                }

                if (spellPart == rootSpellPart) {
                    rootSpellPart = result;
                }

                spellPart = result;
            }
        } else if (revisionContext.getMacros().get(compiled).isDefined()) {
            toBeReplaced = drawingPart;
            revisionContext.updateSpellWithSpell(drawingPart, revisionContext.getMacros().get(compiled).get());
        } else {
            if (patternSize >= 2) {
                drawingPart.glyph = new PatternGlyph(compiled);
            } else {
                drawingPart.glyph = new PatternGlyph();
            }
        }

        drawingPart = null;
        drawingPattern = null;
        revisionContext.updateSpell(rootSpellPart);

        MinecraftClient.getInstance().player.playSoundToPlayer(
                ModSounds.COMPLETE, SoundCategory.MASTER,
                1f, patternSize > 1 ? 1f : 0.6f
        );
    }

    public void replaceCallback(Fragment fragment) {
        if (toBeReplaced != null) {
            toBeReplaced.glyph = fragment;
            toBeReplaced = null;
            revisionContext.updateSpell(rootSpellPart);
        }
    }

    public void updateDrawingPartCallback(Optional<SpellPart> spell) {
        if (toBeReplaced != null) {
            if (spell.isPresent()) {
                toBeReplaced.glyph = spell.get().glyph;
                toBeReplaced.subParts = spell.get().subParts;
            }

            toBeReplaced = null;
            revisionContext.updateSpell(rootSpellPart);
        }
    }

    public boolean isDrawing() {
        return drawingPart != null;
    }

    protected boolean propagateMouseEvent(SpellPart part, double x, double y, double size, double startingAngle, double mouseX, double mouseY, MouseEventHandler callback) {
        var closest = part;
        var closestAngle = startingAngle;
        var closestX = x;
        var closestY = y;
        var closestSize = size;

        var centerAvailable = (isCircleClickable(toLocalSpace(size)) && (drawingPart == null || drawingPart == part)) || part.glyph instanceof SpellPart;
        var closestDistanceSquared = Double.MAX_VALUE;

        int partCount = part.getSubParts().size();
        // We dont change this, its the same for all subcircles
        var nextSize = Math.min(size / 2, size / (double) ((partCount + 1) / 2));
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
                closestAngle = angle;
                closestX = nextX;
                closestY = nextY;
                closestSize = nextSize;
                closestDistanceSquared = distanceSquared;
            }

            i++;
        }

        if (centerAvailable) {
            if (part.glyph instanceof SpellPart centerPart) {
                if (propagateMouseEvent(centerPart, x, y, size / 3, startingAngle, mouseX, mouseY, callback)) {
                    return true;
                }
            } else {
                if (callback.handle(part, toLocalSpace(x), toLocalSpace(y), toLocalSpace(size))) {
                    return true;
                }
            }
        }

        if (Math.sqrt(closestDistanceSquared) <= size && toLocalSpace(size) >= 16) {
            if (closest == part) {
                return false;
            }

            return propagateMouseEvent(closest, closestX, closestY, closestSize, closestAngle, mouseX, mouseY, callback);
        }

        return false;
    }

    private static float toLocalSpace(double value) {
        return (float) (value * PRECISION_OFFSET);
    }

    private static double toScaledSpace(double value) {
        return value / PRECISION_OFFSET;
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

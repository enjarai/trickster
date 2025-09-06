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
    public static final double ZOOM_SPEED = 0.1;

    static final Byte MIDDLE_DOT = 4;
    static final Byte DOT_COUNT = 9;

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

    public Vector2d position;
    public double radius;
    public double windowHeight = 600;

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

    public SpellPartWidget(SpellPart spellPart, double x, double y, double radius, RevisionContext revisionContext, boolean animated) {
        this.rootSpellPart = spellPart;
        this.spellPart = spellPart;
        this.originalPosition = new Vector2d(toScaledSpace(x), toScaledSpace(y));
        this.position = new Vector2d(this.originalPosition);
        this.radius = toScaledSpace(radius);
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
                    this.position = new Vector2d(originalPosition);
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

    public boolean cancelDrawing() {
        if (drawingPart != null) {
            drawingPart.glyph = oldGlyph;
            drawingPart = null;
            drawingPattern = null;
            revisionContext.updateSpell(rootSpellPart);

            MinecraftClient.getInstance().player.playSoundToPlayer(
                    ModSounds.COMPLETE, SoundCategory.MASTER,
                    1f, 0.6f
            );
            return true;
        }
        return false;
    }

    public ScrollAndQuillScreen.PositionMemory saveAndClose() {
        return new ScrollAndQuillScreen.PositionMemory(
                rootSpellPart.hashCode(),
                position, radius,
                rootSpellPart, spellPart,
                new ArrayList<>(parents), new ArrayList<>(angleOffsets));
    }

    public void load(ScrollAndQuillScreen.PositionMemory memory) {
        this.position = memory.position();
        this.radius = memory.radius();
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
        windowHeight = context.getScaledWindowHeight();
        this.renderer.renderPart(
                context.getMatrices(), context.getVertexConsumers(), spellPart,
                position.x, position.y, radius, angleOffsets.peek(), delta,
                radius -> (float) Math.clamp(1 / (radius / windowHeight * 3), 0.0, 0.8),
                new Vec3d(-1, 0, 0)
        );
        context.draw();

        //        context.getMatrices().pop();
    }

    // TODO this still depends on the GUI scale, should be a ratio of the scaled window height of the context
    public static boolean isCircleClickable(double radius) {
        return radius >= 16 && radius <= 256;
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
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) || !Trickster.CONFIG.allowScrollInSpellScreen()) {
            return true;
        }

        zoom(mouseX, mouseY, verticalAmount);

        return true;
    }

    public void zoom(double mouseX, double mouseY, double amount) {
        var minZoom = toScaledSpace(windowHeight * 0.1);

        Vector2d scaledMouse = toScaledSpace(new Vector2d(mouseX, mouseY));
        radius = Math.max(radius + amount * radius * ZOOM_SPEED, minZoom);

        if (radius > minZoom) {
            position.add(new Vector2d(position).sub(scaledMouse).mul(amount * ZOOM_SPEED));
        }

        var subRadius = toLocalSpace(spellPart.subRadius(radius));
        if (amount > 0) {
            if (subRadius > windowHeight && (spellPart.glyph instanceof SpellPart || spellPart.partCount() > 0)) {
                pushNewRoot(scaledMouse);
            }
        } else {
            if (subRadius < windowHeight / 2 && !parents.empty()) {
                popOldRoot();
            }
        }
    }

    private void popOldRoot() {
        var result = parents.pop();
        angleOffsets.pop();

        int partCount = result.partCount();
        var parentRadius = radius * 3;
        int i = 0;

        if (!(result.glyph instanceof SpellPart inner && inner == spellPart)) {
            parentRadius = Math.max(radius * 2, radius * (double) ((partCount + 1) / 2));

            for (var child : result.subParts) {
                if (child == spellPart) {
                    var subPos = result.subPosition(i, parentRadius, angleOffsets.peek());
                    position.sub(subPos);
                    break;
                }

                i++;
            }
        }

        radius = parentRadius;
        spellPart = result;
    }

    private void pushNewRoot(Vector2d scaledMouse) {
        double angle = angleOffsets.peek();
        int closestIndex = closestIndex(spellPart, position, scaledMouse, radius, angle);
        if (closestIndex == -1) {
            this.radius = radius / 3;
            this.angleOffsets.push(angle);
            this.parents.push(spellPart);
            if (spellPart.glyph instanceof SpellPart inner) {
                this.spellPart = inner;
            }
        } else {
            this.position.add(spellPart.subPosition(closestIndex, radius, angle));
            this.radius = spellPart.subRadius(radius);
            this.angleOffsets.push(spellPart.subAngle(closestIndex, angle));
            this.parents.push(spellPart);
            this.spellPart = spellPart.subParts.get(closestIndex);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if (!isDrawing()) {
            position.add(toScaledSpace(new Vector2d(deltaX, deltaY)));
            amountDragged += Math.abs(deltaX) + Math.abs(deltaY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMutable || isDrawing()) {
            if (Trickster.CONFIG.dragDrawing() && button == 0 && !isDrawing()) {
                propagateMouseEvent(mouseX, mouseY, this::selectPattern);
            } else {
                propagateMouseEvent(mouseX, mouseY, (part, pos, rad, mouse) -> true);
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
                if (propagateMouseEvent(mouseX, mouseY, this::selectPattern)) {
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
            propagateMouseEvent(mouseX, mouseY, this::selectPattern);
        }

        super.mouseMoved(mouseX, mouseY);
    }

    // returns the index of the closest sub-circle from the mouse or -1 if the center is the closest
    private static int closestIndex(SpellPart part, Vector2d position, Vector2d mouse, double radius, double angleOffset) {
        int closest = -1;
        double closestDistanceSquared = Double.MAX_VALUE;

        if (part.glyph instanceof SpellPart) {
            closestDistanceSquared = position.distanceSquared(mouse);
        }

        for (int i = 0; i < part.partCount(); i++) {
            Vector2d subPos = part.subPosition(i, radius, angleOffset).add(position);
            double distanceSquared = subPos.distanceSquared(mouse);

            if (distanceSquared < closestDistanceSquared) {
                closest = i;
                closestDistanceSquared = distanceSquared;
            }
        }

        return closest;
    }

    private static boolean areAdjacent(byte a, byte b) {
        if (a == MIDDLE_DOT || b == MIDDLE_DOT) {
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
            for (byte i = 0; i < DOT_COUNT; i++) {
                var move = new ArrayList<Byte>();
                move.add(i);
                moves.put(i, move);
            }
        } else {
            var last = drawingPattern.getLast();
            for (byte i = 0; i < DOT_COUNT; i++) {
                if (i == last) {
                    continue;
                }

                if (!hasLine(i, last)) {
                    var move = new ArrayList<>(drawingPattern);
                    // resolve the middle dot if we are going across
                    if (i == 8 - last) {
                        if (hasLine(i, MIDDLE_DOT) || hasLine(last, MIDDLE_DOT)) {
                            // we are already connected to the middle dot
                            // going across is impossible
                            continue;
                        } else {
                            move.add(MIDDLE_DOT);
                        }
                    }
                    move.add(i);
                    moves.put(i, move);
                } else if (drawingPattern.size() >= 2 && drawingPattern.get(drawingPattern.size() - 2) == i) {
                    var move = new ArrayList<>(drawingPattern);
                    move.removeLast();
                    moves.put(i, move);
                }
            }
        }
        return moves;
    }

    protected boolean selectPattern(SpellPart part, Vector2d position, float radius, Vector2d mouse) {
        if (drawingPart != null && drawingPart != part) {
            // Cancel early if we're already drawing in another part
            return false;
        }

        var patternRadius = radius / PATTERN_TO_PART_RATIO;
        var pixelSize = patternRadius / PART_PIXEL_RADIUS;

        if (drawingPattern == null) {
            drawingPattern = new ArrayList<>();
        }

        var moves = possibleMoves();

        for (byte i = 0; i < DOT_COUNT; i++) {
            // if we are checking the closest neighboring dots on the ring
            // translate the dots outward a bit by enlarging the radius
            // this will make it easier to connect lines to the next neighbors on the ring
            var _patternRadius = patternRadius;
            if (!drawingPattern.isEmpty()) {
                var last = drawingPattern.getLast();
                if (areAdjacent(last, i)) {
                    _patternRadius += pixelSize * Trickster.CONFIG.adjacentPixelCollisionOffset() * 3;
                }
            }

            var pos = getPatternDotPosition((float) position.x, (float) position.y, i, _patternRadius);

            if (isInsideHitbox(pos, pixelSize, mouse.x, mouse.y)) {
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

    protected boolean propagateMouseEvent(double mouseX, double mouseY, MouseEventHandler callback) {
        return propagateMouseEvent(spellPart, position, radius, angleOffsets.peek(), toScaledSpace(new Vector2d(mouseX, mouseY)), callback);
    }

    protected boolean propagateMouseEvent(SpellPart part, Vector2d pos, double radius, double startingAngle, Vector2d mouse, MouseEventHandler callback) {
        int closestIndex = closestIndex(part, pos, mouse, radius, startingAngle);
        boolean centerAvailable = (isCircleClickable(toLocalSpace(radius)) && (drawingPart == null || drawingPart == part))
                || part.glyph instanceof SpellPart;

        SpellPart closest = part;
        double closestDistanceSquared = Double.MAX_VALUE;
        Vector2d closestPosition = pos;
        double closestRadius = radius;
        double closestAngle = startingAngle;

        if (closestIndex > -1) {
            closest = part.subParts.get(closestIndex);
            closestPosition = part.subPosition(closestIndex, radius, startingAngle).add(pos);
            closestDistanceSquared = closestPosition.distanceSquared(mouse);
            closestAngle = part.subAngle(closestIndex, startingAngle);
            closestRadius = part.subRadius(radius);
        }

        if (centerAvailable) {
            if (part.glyph instanceof SpellPart inner) {
                if (propagateMouseEvent(inner, pos, radius / 3, startingAngle, mouse, callback)) {
                    return true;
                }
            } else {
                if (callback.handle(part, toLocalSpace(pos), toLocalSpace(radius), toLocalSpace(mouse))) {
                    return true;
                }
            }
        }

        if (Math.sqrt(closestDistanceSquared) <= radius && toLocalSpace(radius) >= 16) {
            if (closest == part) {
                return false;
            }

            return propagateMouseEvent(closest, closestPosition, closestRadius, closestAngle, mouse, callback);
        }

        return false;
    }

    private static float toLocalSpace(double value) {
        return (float) (value * PRECISION_OFFSET);
    }

    private static double toScaledSpace(double value) {
        return value / PRECISION_OFFSET;
    }

    private static Vector2d toLocalSpace(Vector2d value) {
        return new Vector2d(value).mul(PRECISION_OFFSET);
    }

    private static Vector2d toScaledSpace(Vector2d value) {
        return new Vector2d(value).div(PRECISION_OFFSET);
    }

    interface MouseEventHandler {
        boolean handle(SpellPart part, Vector2d pos, float radius, Vector2d mouse);
    }

    //    @Override
    //    public void setX(int x) {
    //        this.x = x + radius;
    //    }
    //
    //    @Override
    //    public void setY(int y) {
    //        this.y = y + radius;
    //    }
    //
    //    @Override
    //    public int getX() {
    //        return (int) (x - radius);
    //    }
    //
    //    @Override
    //    public int getY() {
    //        return (int) (y - radius);
    //    }
    //
    //    @Override
    //    public int getWidth() {
    //        return (int) radius * 2;
    //    }
    //
    //    @Override
    //    public int getHeight() {
    //        return (int) radius * 2;
    //    }
    //
    //    @Override
    //    public void forEachChild(Consumer<ClickableWidget> consumer) {
    //
    //    }
}

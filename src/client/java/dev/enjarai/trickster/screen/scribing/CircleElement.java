package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.enjarai.trickster.render.CircleRenderer.getPatternDotPosition;
import static dev.enjarai.trickster.render.CircleRenderer.isInsideHitbox;
import static dev.enjarai.trickster.render.SpellCircleRenderer.PART_PIXEL_RADIUS;
import static dev.enjarai.trickster.render.SpellCircleRenderer.PATTERN_TO_PART_RATIO;

public class CircleElement implements Element, Drawable, Selectable {
    static final Byte MIDDLE_DOT = 4;
    static final Byte DOT_COUNT = 9;

    static final Byte[] RING_ORDER = {
        (byte) 0, (byte) 1, (byte) 2, (byte) 5, (byte) 8, (byte) 7, (byte) 6, (byte) 3
    };
    static final Byte[] RING_INDICES = {
        (byte) 0, (byte) 1, (byte) 2, (byte) 7, (byte) 0, (byte) 3, (byte) 6, (byte) 5, (byte) 4
    };

    public static final double FORK_THRESHOLD = 50;
    public static final double DISCARD_THRESHOLD = 5;
    public static final double ZOOM_SPEED = 0.1;

    private final ScribingScreen screen;
    private final SpellPart part;
    @Nullable
    private final CircleElement parentCircle;
    private final List<CircleElement> childCircles = new ArrayList<>();

    private double canonicalX;
    private double canonicalY;
    private double canonicalRadius;
    private double animationX;
    private double animationY;
    private double animationRadius;

    private long animationStart;

    private double startingAngle;

    @Nullable
    private List<Byte> drawingPattern;

    public CircleElement(ScribingScreen screen, SpellPart part, @Nullable CircleElement parentCircle, double x, double y, double radius, double startingAngle) {
        this.screen = screen;
        this.part = part;
        this.parentCircle = parentCircle;
        this.canonicalX = x;
        this.canonicalY = y;
        this.canonicalRadius = radius;
        this.animationX = x;
        this.animationY = y;
        this.animationRadius = radius;
        this.startingAngle = startingAngle;
    }

    public void initialize() {
        if (canonicalRadius > FORK_THRESHOLD) {
            fork();
        }
        // probably dont need this
        //        if (radius < DISCARD_THRESHOLD) {
        //            discard();
        //        }
    }

    public void updatePosition(double x, double y, double radius) {
        this.canonicalX = x;
        this.canonicalY = y;
        if (radius > FORK_THRESHOLD && this.canonicalRadius < FORK_THRESHOLD) {
            fork();
        }
        this.canonicalRadius = radius;
        if (radius < DISCARD_THRESHOLD) {
            discard();
        }
        animationX = getAnimatedX();
        animationY = getAnimatedY();
        animationRadius = getAnimatedRadius();
        animationStart = screen.getCurrentTime();
    }

    private void fork() {
        List.copyOf(childCircles).forEach(CircleElement::discard);
        childCircles.clear();

        int i = 0;
        for (var child : part.subParts) {
            var angle = part.subAngle(i, startingAngle);
            var subX = canonicalX + (canonicalRadius * Math.cos(angle));
            var subY = canonicalY + (canonicalRadius * Math.sin(angle));

            var circle = new CircleElement(
                screen, child, this,
                subX, subY, part.subRadius(canonicalRadius),
                angle
            );
            var animationRadius = getAnimatedRadius();
            circle.applyAnimationState(
                getAnimatedX() + (animationRadius * Math.cos(angle)),
                getAnimatedY() + (animationRadius * Math.sin(angle)),
                part.subRadius(animationRadius)
            );

            childCircles.add(circle);
            screen.addCircle(circle);

            i++;
        }

        if (part.glyph instanceof SpellPart inner) {
            var circle = new CircleElement(
                screen, inner, this,
                canonicalX, canonicalY, canonicalRadius / 3,
                startingAngle
            );
            circle.applyAnimationState(
                getAnimatedX(), getAnimatedY(), getAnimatedRadius() / 3
            );

            childCircles.add(circle);
            screen.addCircle(circle);
        }
    }

    private void discard() {
        if (parentCircle != null) {
            screen.removeCircle(this);
            parentCircle.dropChild(this);
            List.copyOf(childCircles).forEach(CircleElement::discard);
            childCircles.clear();
        }
    }

    private void dropChild(CircleElement circle) {
        childCircles.remove(circle);
    }

    public void zoom(double mouseX, double mouseY, double amount) {
        var newRadius = canonicalRadius + amount * canonicalRadius * ZOOM_SPEED;

        var newX = canonicalX + (canonicalX - mouseX) * amount * ZOOM_SPEED;
        var newY = canonicalY + (canonicalY - mouseY) * amount * ZOOM_SPEED;

        //        animationRadius += amount * animationRadius * ZOOM_SPEED;
        //        animationX += (animationX - mouseX) * amount * ZOOM_SPEED;
        //        animationY += (animationY - mouseY) * amount * ZOOM_SPEED;

        updatePosition(newX, newY, newRadius);
    }

    public boolean click(double mouseX, double mouseY, int button) {
        var dot = getHoveredDot(mouseX, mouseY);
        if (dot != null && button == 0) {
            startDrawing().add(dot);
        }

        return false;
    }

    public void movingDrawing(double mouseX, double mouseY) {
        var dot = getHoveredDot(mouseX, mouseY);
        if (dot == null || drawingPattern == null || dot.equals(drawingPattern.getLast())) {
            return;
        }

        var moves = possibleMoves(drawingPattern);
        if (moves.get(dot) != null) {
            boolean removing = drawingPattern.size() > moves.get(dot).size();
            drawingPattern = moves.get(dot);
            // TODO click sound?
            //            MinecraftClient.getInstance().player.playSoundToPlayer(
            //                    ModSounds.DRAW, SoundCategory.MASTER,
            //                    1f, ModSounds.randomPitch(removing ? 0.6f : 1.0f, 0.2f)
            //            );
        }
    }

    public void finishDrawing(boolean apply) {
        if (drawingPattern == null) {
            return;
        }

        if (apply) {
            if (drawingPattern.size() >= 2) {
                part.glyph = new PatternGlyph(Pattern.from(drawingPattern));
            } else {
                part.glyph = new PatternGlyph();
            }
        }

        //        MinecraftClient.getInstance().player.playSoundToPlayer(
        //                ModSounds.COMPLETE, SoundCategory.MASTER,
        //                1f, drawingPattern.size() > 1 ? 1f : 0.6f
        //        );

        drawingPattern = null;
        screen.setDrawingCircle(null);
    }

    private List<Byte> startDrawing() {
        drawingPattern = new ArrayList<>();
        screen.setDrawingCircle(this);
        return drawingPattern;
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

    private Byte getHoveredDot(double mouseX, double mouseY) {
        for (byte i = 0; i < DOT_COUNT; i++) {
            // if we are checking the closest neighboring dots on the ring
            // translate the dots outward a bit by enlarging the radius
            // this will make it easier to connect lines to the next neighbors on the ring
            var patternRadius = getAnimatedRadius() / PATTERN_TO_PART_RATIO;
            var pixelSize = patternRadius / PART_PIXEL_RADIUS;

            if (drawingPattern != null && !drawingPattern.isEmpty()) {
                var last = drawingPattern.getLast();
                if (areAdjacent(last, i)) {
                    patternRadius += pixelSize * Trickster.CONFIG.adjacentPixelCollisionOffset() * 3;
                }
            }

            var pos = getPatternDotPosition((float) getAnimatedX(), (float) getAnimatedY(), i, (float) patternRadius);

            if (isInsideHitbox(pos, (float) pixelSize, mouseX, mouseY)) {
                return i;
            }
        }
        return null;
    }

    // generates all possible moves from the current drawingPattern
    // assigns the target dot with the resulting next drawingPattern
    private HashMap<Byte, List<Byte>> possibleMoves(List<Byte> drawingPattern) {
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

    private void applyAnimationState(double animationX, double animationY, double animationRadius) {
        animationStart = screen.getCurrentTime();
        this.animationX = animationX;
        this.animationY = animationY;
        this.animationRadius = animationRadius;
    }

    private double animate(double canonicalV, double animationV) {
        var weight = canonicalRadius / 2;
        var delta = canonicalV - animationV;
        var progress = (screen.getCurrentTime() - animationStart) / weight * delta;
        if (delta > 0) {
            animationV += MathHelper.clamp(progress, 0, delta);
        } else {
            animationV += MathHelper.clamp(progress, delta, 0);
        }
        return animationV;
    }

    public double getAnimatedX() {
        return animate(canonicalX, animationX);
    }

    public double getAnimatedY() {
        return animate(canonicalY, animationY) + Math.sin(screen.getCurrentTime() / 1000d);
    }

    public double getAnimatedRadius() {
        return animate(canonicalRadius, animationRadius);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var x = getAnimatedX();
        var y = getAnimatedY();
        var radius = getAnimatedRadius();

        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0, 0, 1 / radius);
        //        screen.renderer.renderCircle(
        //                matrices, part,
        //                x, y, radius, startingAngle, delta, // maybe we dont need angle here
        //                r -> Math.clamp(1 / (r / screen.height * 3), 0.0, 0.8),
        //                new Vec3d(-1, 0, 0), drawingPattern
        //        );
        matrices.pop();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        var activeRadius = getAnimatedRadius() / 2;
        var diffX = mouseX - getAnimatedX();
        var diffY = mouseY - getAnimatedY();
        return diffX * diffX + diffY * diffY < activeRadius * activeRadius;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public double getRadius() {
        return canonicalRadius;
    }
}

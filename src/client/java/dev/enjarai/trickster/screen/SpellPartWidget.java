package dev.enjarai.trickster.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpellPartWidget extends AbstractParentElement implements Drawable, Selectable {
    public static final Identifier CIRCLE_TEXTURE = Trickster.id("textures/gui/circle_48.png");
    public static final Identifier CIRCLE_TEXTURE_HALF = Trickster.id("textures/gui/circle_24.png");
    public static final float PATTERN_TO_PART_RATIO = 2.5f;
    public static final int PART_PIXEL_RADIUS = 24;
    public static final int CLICK_HITBOX_SIZE = 6;

    public static final Pattern CREATE_SUBCIRCLE_GLYPH = Pattern.of(0, 4, 8, 7);
    public static final Pattern CREATE_GLYPH_CIRCLE_GLYPH = Pattern.of(0, 4, 8, 5);
    public static final Pattern DELETE_CIRCLE_GLYPH = Pattern.of(0, 4, 8);
    public static final Pattern CLEAR_DISABLED_GLYPH = Pattern.of(0, 4, 8, 5, 2, 1, 0, 3, 6, 7, 8);

    private SpellPart spellPart;
//    private List<SpellPartWidget> partWidgets;

    public double x;
    public double y;
    public double size;
    private double amountDragged;

    private Consumer<SpellPart> updateListener;

    private SpellPart drawingPart;
    private List<Byte> drawingPattern;

    public SpellPartWidget(SpellPart spellPart, double x, double y, double size, Consumer<SpellPart> updateListener) {
        this.spellPart = spellPart;
        this.x = x;
        this.y = y;
        this.size = size;
        this.updateListener = updateListener;
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
        renderPart(context, Optional.of(spellPart), (float) x, (float) y, (float) size, 0, mouseX, mouseY, delta);
    }

    protected void renderPart(DrawContext context, Optional<SpellPart> entry, float x, float y, float size, double startingAngle, int mouseX, int mouseY, float delta) {
        var alpha = Math.clamp(1 / (size / context.getScaledWindowHeight() * 2) - 0.2, 0, 1);

        if (entry.isPresent()) {
            var part = entry.get();

            drawTexturedQuad(
                    context, CIRCLE_TEXTURE,
                    x - size, x + size, y - size, y + size,
                    0,
                    1f, 1f, 1f, (float) alpha
            );
            drawGlyph(
                    context, part,
                    x, y, size, startingAngle,
                    mouseX, mouseY, delta
            );

            int partCount = part.getSubParts().size();

            drawDivider(context, x, y, startingAngle, size, partCount, (float) alpha);

            int i = 0;
            for (var child : part.getSubParts()) {
                var angle = startingAngle + (2 * Math.PI) / partCount * i - (Math.PI / 2);

                var nextX = x + (size * Math.cos(angle));
                var nextY = y + (size * Math.sin(angle));

                var nextSize = Math.min(size / 2, size / (float) (partCount / 2));

                renderPart(context, child, (float) nextX, (float) nextY, nextSize, angle, mouseX, mouseY, delta);

                i++;
            }
        } else {
            drawTexturedQuad(
                    context, CIRCLE_TEXTURE_HALF,
                    x - size / 2, x + size / 2, y - size / 2, y + size / 2,
                    0,
                    1f, 1f, 1f, (float) alpha
            );
        }
    }

    protected void drawDivider(DrawContext context, float x, float y, double startingAngle, float size, float partCount, float alpha) {
        var pixelSize = size / PART_PIXEL_RADIUS;
        var lineAngle = startingAngle + (2 * Math.PI) / partCount * -0.5 - (Math.PI / 2);

        float lineX = (float) (x + (size * Math.cos(lineAngle)));
        float lineY = (float) (y + (size * Math.sin(lineAngle)));

        var toCenterVec = new Vector2f(lineX - x, lineY - y).normalize();
        var perpendicularVec = new Vector2f(toCenterVec).perpendicular();
        toCenterVec.mul(pixelSize * 6);
        perpendicularVec.mul(pixelSize * 0.5f);

        drawFlatPolygon(context, c -> {
            c.accept(lineX - perpendicularVec.x + toCenterVec.x * 0.5f, lineY - perpendicularVec.y + toCenterVec.y * 0.5f);
            c.accept(lineX + perpendicularVec.x + toCenterVec.x * 0.5f, lineY + perpendicularVec.y + toCenterVec.y * 0.5f);
            c.accept(lineX + perpendicularVec.x - toCenterVec.x, lineY + perpendicularVec.y - toCenterVec.y);
            c.accept(lineX - perpendicularVec.x - toCenterVec.x, lineY - perpendicularVec.y - toCenterVec.y);
        }, 0, 0.5f, 0.5f, 1, alpha * 0.2f);

//        drawTexturedQuad(
//                context, CIRCLE_TEXTURE_HALF,
//                lineX - size / 4, lineX + size / 4, lineY - size / 4, lineY + size / 4,
//                0,
//                0.5f, 0.5f, 1f, alpha
//        );
    }

    protected void drawGlyph(DrawContext context, SpellPart parent, float x, float y, float size, double startingAngle, int mouseX, int mouseY, float delta) {
        var glyph = parent.getGlyph();
        if (glyph instanceof SpellPart part) {
            renderPart(context, Optional.of(part), x, y, size / 3, startingAngle, mouseX, mouseY, delta);
        } else if (glyph instanceof PatternGlyph pattern) {
            var patternSize = size / PATTERN_TO_PART_RATIO;
            var pixelSize = patternSize / PART_PIXEL_RADIUS;

            var isDrawing = drawingPart == parent;
            var patternList = isDrawing ? drawingPattern : pattern.orderedPattern();

            for (int i = 0; i < 9; i++) {
                var pos = getPatternDotPosition(x, y, i, patternSize);

                var isLinked = patternList.contains(Integer.valueOf(i).byteValue());
                float dotScale = 1;

                if (isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(size)) {
                    dotScale = 1.6f;
                } else if (!isLinked) {
                    if (isCircleClickable(size)) {
                        var mouseDistance = new Vector2f(mouseX - pos.x, mouseY - pos.y).length();
                        dotScale = Math.clamp(patternSize / mouseDistance - 0.2f, 0, 1);
                    } else {
                        // Skip the dot if its too small to click
                        continue;
                    }
                }

                var dotSize = pixelSize * dotScale;

                drawFlatPolygon(context, c -> {
                    c.accept(pos.x - dotSize, pos.y - dotSize);
                    c.accept(pos.x - dotSize, pos.y + dotSize);
                    c.accept(pos.x + dotSize, pos.y + dotSize);
                    c.accept(pos.x + dotSize, pos.y - dotSize);
                }, 0, isDrawing && isLinked ? 0.5f : 1, isDrawing && isLinked ? 0.5f : 1, 1, 0.5f);
            }

            Vector2f last = null;
            for (var b : patternList) {
                var now = getPatternDotPosition(x, y, b, patternSize);
                if (last != null) {
                    drawGlyphLine(context, last, now, pixelSize, isDrawing);
                }
                last = now;
            }

            if (isDrawing && last != null) {
                var now = new Vector2f(mouseX, mouseY);
                drawGlyphLine(context, last, now, pixelSize, true);
            }
        }
    }

    protected void drawGlyphLine(DrawContext context, Vector2f last, Vector2f now, float pixelSize, boolean isDrawing) {
        var parallelVec = new Vector2f(last.y - now.y, now.x - last.x).normalize().mul(pixelSize / 2);
        var directionVec = new Vector2f(last.x - now.x, last.y - now.y).normalize().mul(pixelSize * 3);

        drawFlatPolygon(context, c -> {
            c.accept(last.x - parallelVec.x - directionVec.x, last.y - parallelVec.y - directionVec.y);
            c.accept(last.x + parallelVec.x - directionVec.x, last.y + parallelVec.y - directionVec.y);
            c.accept(now.x + parallelVec.x + directionVec.x, now.y + parallelVec.y + directionVec.y);
            c.accept(now.x - parallelVec.x + directionVec.x, now.y - parallelVec.y + directionVec.y);
        }, 0, isDrawing ? 0.5f : 1, isDrawing ? 0.5f : 1, 1, 0.5f);
    }

    static Vector2f getPatternDotPosition(float x, float y, int i, float size) {
        float xSign = (float) (i % 3 - 1);
        float ySign = (float) (i / 3 - 1);

        if (xSign != 0 && ySign != 0) {
            xSign *= 0.7f;
            ySign *= 0.7f;
        }

        return new Vector2f(
                x + xSign * size,
                y + ySign * size
        );
    }

    static boolean isInsideHitbox(Vector2f pos, float pixelSize, double mouseX, double mouseY) {
        var hitboxSize = CLICK_HITBOX_SIZE * pixelSize;
        return mouseX >= pos.x - hitboxSize && mouseX <= pos.x + hitboxSize &&
                mouseY >= pos.y - hitboxSize && mouseY <= pos.y + hitboxSize;
    }

    static void drawTexturedQuad(DrawContext context, Identifier texture, float x1, float x2, float y1, float y2, float z, float r, float g, float b, float alpha) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, alpha);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture((float) 0, (float) 0);
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture((float) 0, (float) 1);
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture((float) 1, (float) 1);
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture((float) 1, (float) 0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    static void drawFlatPolygon(DrawContext context, Consumer<BiConsumer<Float, Float>> vertexProvider, float z, float r, float g, float b, float alpha) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexProvider.accept((x, y) -> vertexConsumer.vertex(matrix4f, x, y, z).color(r, g, b, alpha));
        context.draw();
    }

    protected static boolean isCircleClickable(float size) {
        return size >= 16;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
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

        var intensity = verticalAmount * size / 10;
        size += intensity;
        x += verticalAmount * (x - mouseX) / 10;
        y += verticalAmount * (y - mouseY) / 10;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if (!isDrawing()) {
            x += deltaX;
            y += deltaY;

            amountDragged += Math.abs(deltaX) + Math.abs(deltaY);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // We need to return true on the mouse down event to make sure the screen knows if we're on a clickable node
        if (propagateMouseEvent(spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY,
                (part, x, y, size) -> true)) {
            return true;
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var dragged = amountDragged;
        amountDragged = 0;
        if (dragged > 5) {
            return false;
        }

        if (button == 0 && !isDrawing()) {
            if (propagateMouseEvent(spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY,
                    (part, x, y, size) -> selectPattern(part, x, y, size, mouseX, mouseY))) {
                return true;
            }
        }

        if (drawingPart != null) {
            stopDrawing();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isDrawing()) {
            propagateMouseEvent(spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY,
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
                    part.glyph = new PatternGlyph(List.of());
                    drawingPattern = new ArrayList<>();
                }

                if (drawingPattern.size() >= 2 && drawingPattern.get(drawingPattern.size() - 2) == (byte) i) {
                    drawingPattern.removeLast();
                } else if (drawingPattern.isEmpty() ||
                        (drawingPattern.getLast() != (byte) i && !hasOverlappingLines(drawingPattern, drawingPattern.getLast(), (byte) i))) {
                    drawingPattern.add((byte) i);
                    // TODO click sound?
                }

                return true;
            }
        }

        return false;
    }

    protected void stopDrawing() {
        var compiled = Pattern.from(drawingPattern);

        if (compiled.equals(CREATE_SUBCIRCLE_GLYPH)) {
            drawingPart.subParts.add(Optional.of(new SpellPart()));
        } else if (compiled.equals(CREATE_GLYPH_CIRCLE_GLYPH)) {
            drawingPart.glyph = new SpellPart();
        } else if (compiled.equals(DELETE_CIRCLE_GLYPH)) {
            deleteSubPartFromTree(drawingPart, spellPart);
        } else if (compiled.equals(CLEAR_DISABLED_GLYPH)) {
            drawingPart.subParts.removeIf(Optional::isEmpty);
        } else if (drawingPattern.size() > 1) {
            drawingPart.glyph = new PatternGlyph(compiled, drawingPattern);
        }

        drawingPart = null;
        drawingPattern = null;

        updateListener.accept(spellPart);
    }

    public boolean isDrawing() {
        return drawingPart != null;
    }

    protected boolean deleteSubPartFromTree(SpellPart target, SpellPart current) {
        if (current.glyph instanceof SpellPart part) {
            if (part == target) {
                current.glyph = new PatternGlyph();
                return true;
            }

            if (deleteSubPartFromTree(target, part)) {
                return true;
            }
        }

        int i = 0;
        for (var part : current.subParts) {
            if (part.isPresent()) {
                if (part.get() == target) {
                    current.subParts.set(i, Optional.empty());
                    return true;
                }

                if (deleteSubPartFromTree(target, part.get())) {
                    return true;
                }
            }
            i++;
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

    protected static boolean propagateMouseEvent(SpellPart part, float x, float y, float size, float startingAngle, double mouseX, double mouseY, MouseEventHandler callback) {
        var closest = part;
        var closestAngle = startingAngle;
        var closestX = x;
        var closestY = y;
        var closestSize = size;

        // These two dont need to be updated for the actual closest
        var initialDiffX = x - mouseX;
        var initialDiffY = y - mouseY;

        var closestDistanceSquared = initialDiffX * initialDiffX + initialDiffY * initialDiffY;

        int partCount = part.getSubParts().size();
        // Dont change this, its the same for all subcircles
        var nextSize = Math.min(size / 2, size / (float) (partCount / 2));
        int i = 0;
        for (var child : part.getSubParts()) {
            if (child.isPresent()) {
                var childPart = child.get();

                var angle = startingAngle + (2 * Math.PI) / partCount * i - (Math.PI / 2);

                var nextX = x + (size * Math.cos(angle));
                var nextY = y + (size * Math.sin(angle));
                var diffX = nextX - mouseX;
                var diffY = nextY - mouseY;
                var distanceSquared = diffX * diffX + diffY * diffY;

                if (distanceSquared < closestDistanceSquared) {
                    closest = childPart;
                    closestAngle = (float) angle;
                    closestX = (float) nextX;
                    closestY = (float) nextY;
                    closestSize = nextSize;
                    closestDistanceSquared = distanceSquared;
                }
            }

            i++;
        }

        if (Math.sqrt(closestDistanceSquared) <= size && isCircleClickable(closestSize)) {
            if (closest == part) {
                // Special handling for part glyphs, because of course
                // This makes it impossible to interact with direct parents of part glyphs, but thats not an issue
                if (closest.glyph instanceof SpellPart centerPart) {
                    closest = centerPart;
                    closestSize /= 3;
                } else {
                    return callback.handle(closest, closestX, closestY, closestSize);
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

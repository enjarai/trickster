package dev.enjarai.trickster.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Glyph;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpellPartWidget extends AbstractParentElement implements Drawable, Selectable {
    public static final Identifier CIRCLE_TEXTURE = Trickster.id("textures/gui/circle_48.png");
    public static final float PATTERN_TO_PART_RATIO = 2.2f;
    public static final int PART_PIXEL_RADIUS = 24;
    public static final int CLICK_HITBOX_SIZE = 6;

    private SpellPart spellPart;
//    private List<SpellPartWidget> partWidgets;

    public double x;
    public double y;
    public double size;

    private SpellPart drawingPart;
    private List<Byte> drawingPattern;

    public SpellPartWidget(SpellPart spellPart, double x, double y, double size) {
        this.spellPart = spellPart;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    public void setSpellPart(SpellPart spellPart) {
        this.spellPart = spellPart;
//        partWidgets.clear();
//        spellPart.
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderPart(context, spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY, delta);
    }

    protected void renderPart(DrawContext context, SpellPart part, float x, float y, float size, double startingAngle, int mouseX, int mouseY, float delta) {
        var alpha = Math.clamp(1 / (size / context.getScaledWindowHeight() * 2) - 0.2, 0, 1);
        drawTexturedQuad(
                context, CIRCLE_TEXTURE,
                x - size, x + size, y - size, y + size,
                0, 0, 1, 0, 1,
                (float) alpha
        );
        drawGlyph(
                context, part,
                x, y, size, startingAngle,
                mouseX, mouseY, delta
        );

        int partCount = part.getSubParts().size();
        int i = 0;
        for (var child : part.getSubParts()) {
            i++;

            if (child.isPresent()) {
                var childPart = child.get();

                var angle = startingAngle + (2 * Math.PI) / partCount * i - (Math.PI / 2);

                var nextX = x + (size * Math.cos(angle));
                var nextY = y + (size * Math.sin(angle));

                var nextSize = Math.min(size / 2, size / (partCount - 1));

                renderPart(context, childPart, (float) nextX, (float) nextY, nextSize, angle, mouseX, mouseY, delta);
            }
        }
    }

    protected void drawGlyph(DrawContext context, SpellPart parent, float x, float y, float size, double startingAngle, int mouseX, int mouseY, float delta) {
        var glyph = parent.getGlyph();
        if (glyph instanceof SpellPart part) {
            renderPart(context, part, x, y, size / 2, startingAngle, mouseX, mouseY, delta);
        } else if (glyph instanceof PatternGlyph pattern) {
            var patternSize = size / PATTERN_TO_PART_RATIO;
            var pixelSize = patternSize / PART_PIXEL_RADIUS;

            var isDrawing = drawingPart == parent;
            var patternList = isDrawing ? drawingPattern : pattern.pattern();

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

    static void drawTexturedQuad(DrawContext context, Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2, float alpha) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // We need to return true on the mouse down event to make sure the screen knows if we're on a clickable node
        if (propagateMouseEvent(spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY,
                (part, x, y, size) -> true)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (propagateMouseEvent(spellPart, (float) x, (float) y, (float) size, 0, mouseX, mouseY,
                (part, x, y, size) -> clickedPart(part, x, y, size, mouseX, mouseY, button))) {
            return true;
        }

        if (drawingPart != null) {
            stopDrawing();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected boolean clickedPart(SpellPart part, float x, float y, float size, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

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

                if (!drawingPattern.contains(Integer.valueOf(i).byteValue())) {
                    drawingPattern.add((byte) i);
                    // TODO click sound?
                }

                return true;
            }
        }

        return false;
    }

    protected void stopDrawing() {
        drawingPart.glyph = new PatternGlyph(drawingPattern);
        drawingPart = null;
        drawingPattern = null;
    }

    public boolean isDrawing() {
        return drawingPart != null;
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
        var nextSize = Math.min(size / 2, size / (partCount - 1));
        int i = 0;
        for (var child : part.getSubParts()) {
            i++;

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
        }

        if (Math.sqrt(closestDistanceSquared) <= size && isCircleClickable(closestSize)) {
            if (closest == part) {
                // Special handling for part glyphs, because of course
                // This makes it impossible to interact with direct parents of part glyphs, but thats not an issue
                if (closest.glyph instanceof SpellPart centerPart) {
                    closest = centerPart;
                    closestSize /= 2;
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

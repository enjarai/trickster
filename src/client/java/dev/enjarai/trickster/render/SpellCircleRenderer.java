package dev.enjarai.trickster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.enjarai.trickster.screen.SpellPartWidget.PRECISION_OFFSET;
import static dev.enjarai.trickster.screen.SpellPartWidget.isCircleClickable;

public class SpellCircleRenderer {
    public static final Identifier CIRCLE_TEXTURE = Trickster.id("textures/gui/circle_48.png");
    public static final Identifier CIRCLE_TEXTURE_HALF = Trickster.id("textures/gui/circle_24.png");
    public static final float PATTERN_TO_PART_RATIO = 2.5f;
    public static final int PART_PIXEL_RADIUS = 24;
    public static final int CLICK_HITBOX_SIZE = 6;

    private final boolean inUI;
    private final boolean inEditor;
    private final double precisionOffset;

    private Supplier<SpellPart> drawingPartGetter;
    private Supplier<List<Byte>> drawingPatternGetter;
    private double mouseX;
    private double mouseY;

    private float r = 1f, g = 1f, b = 1f;
    private float circleTransparency = 1f;

    public SpellCircleRenderer(Boolean inUI, double precisionOffset) {
        this.inUI = inUI;
        this.inEditor = false;
        this.precisionOffset = precisionOffset;
    }

    public SpellCircleRenderer(Supplier<SpellPart> drawingPartGetter, Supplier<List<Byte>> drawingPatternGetter, double precisionOffset) {
        this.drawingPartGetter = drawingPartGetter;
        this.drawingPatternGetter = drawingPatternGetter;
        this.inUI = true;
        this.inEditor = true;
        this.precisionOffset = precisionOffset;
    }

    public void setMousePosition(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setCircleTransparency(float circleTransparency) {
        this.circleTransparency = circleTransparency;
    }

    private float toLocalSpace(double value) {
        return (float) (value * precisionOffset);
    }

    public void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart entry, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal) {
        var alpha = alphaGetter.apply(toLocalSpace(size));

        drawTexturedQuad(
                matrices, vertexConsumers, CIRCLE_TEXTURE,
                toLocalSpace(x - size), toLocalSpace(x + size), toLocalSpace(y - size), toLocalSpace(y + size),
                0,
                r, g, b, alpha * circleTransparency, normal
        );
        drawGlyph(
                matrices, vertexConsumers, entry,
                x, y, size, startingAngle,
                delta, alphaGetter, normal
        );

        int partCount = entry.getSubParts().size();


        drawDivider(matrices, vertexConsumers, toLocalSpace(x), toLocalSpace(y), startingAngle, toLocalSpace(size), partCount, alpha);

        if (!inUI && size < 0.01f) {
            return;
        }

        matrices.push();
        if (!inUI) {
            matrices.translate(0, 0, -size / 8f);
        }
        int i = 0;
        for (var child : entry.getSubParts()) {
            var angle = startingAngle + (2 * Math.PI) / partCount * i - (Math.PI / 2);

            var nextX = x + (size * Math.cos(angle));
            var nextY = y + (size * Math.sin(angle) );

            var nextSize = Math.min(size / 2, size / (double) ((partCount + 1) / 2));

            renderPart(matrices, vertexConsumers, child, nextX, nextY, nextSize, angle, delta, alphaGetter, normal);

            i++;
        }
        matrices.pop();
    }

    protected void drawDivider(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, double startingAngle, float size, float partCount, float alpha) {
        var pixelSize = size / PART_PIXEL_RADIUS;
        var lineAngle = startingAngle + (2 * Math.PI) / partCount * -0.5 - (Math.PI / 2);

        float lineX = (float) (x + (size * Math.cos(lineAngle)));
        float lineY = (float) (y + (size * Math.sin(lineAngle)));

        var toCenterVec = new Vector2f(lineX - x, lineY - y).normalize();
        var perpendicularVec = new Vector2f(toCenterVec).perpendicular();
        toCenterVec.mul(pixelSize * 6);
        perpendicularVec.mul(pixelSize * 0.5f);

        drawFlatPolygon(matrices, vertexConsumers, c -> {
            c.accept(lineX - perpendicularVec.x + toCenterVec.x * 0.5f, lineY - perpendicularVec.y + toCenterVec.y * 0.5f);
            c.accept(lineX + perpendicularVec.x + toCenterVec.x * 0.5f, lineY + perpendicularVec.y + toCenterVec.y * 0.5f);
            c.accept(lineX + perpendicularVec.x - toCenterVec.x, lineY + perpendicularVec.y - toCenterVec.y);
            c.accept(lineX - perpendicularVec.x - toCenterVec.x, lineY - perpendicularVec.y - toCenterVec.y);
        }, 0, 0.5f * r, 0.5f * g, 1 * b, alpha * 0.2f);

//        drawTexturedQuad(
//                context, CIRCLE_TEXTURE_HALF,
//                lineX - size / 4, lineX + size / 4, lineY - size / 4, lineY + size / 4,
//                0,
//                0.5f, 0.5f, 1f, alpha
//        );
    }

    protected void drawGlyph(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart parent, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal) {
        var glyph = parent.getGlyph();
        if (glyph instanceof SpellPart part) {
            renderPart(matrices, vertexConsumers, part, x, y, size / 3, startingAngle, delta, alphaGetter, normal);
        } else {
            matrices.push();
            drawSide(matrices, vertexConsumers, parent, toLocalSpace(x), toLocalSpace(y), toLocalSpace(size), alphaGetter, glyph);
            matrices.pop();

            if (!inUI) {
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                drawSide(matrices, vertexConsumers, parent, toLocalSpace(-x), toLocalSpace(y), toLocalSpace(size), alphaGetter, glyph);
                matrices.pop();
            }
        }
    }

    private void drawSide(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart parent, float x, float y, float size, Function<Float, Float> alphaGetter, Fragment glyph) {
        var alpha = alphaGetter.apply(size);
        var patternSize = size / PATTERN_TO_PART_RATIO;
        var pixelSize = patternSize / PART_PIXEL_RADIUS;

        if (glyph instanceof PatternGlyph pattern) {

            var isDrawing = inEditor && drawingPartGetter.get() == parent;
            var drawingPattern = inEditor ? drawingPatternGetter.get() : null;
            var patternList = isDrawing ? Pattern.from(drawingPattern) : pattern.pattern();

            for (int i = 0; i < 9; i++) {
                var pos = getPatternDotPosition(x, y, i, patternSize);

                var isLinked = isDrawing ? drawingPattern.contains((byte) i) : patternList.contains(i);
                float dotScale = 1;

                if (inEditor && isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(size)) {
                    dotScale = 1.6f;
                } else if (!isLinked) {
                    if (inEditor && isCircleClickable(size)) {
                        var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                        dotScale = Math.clamp(patternSize / mouseDistance - 0.2f, 0, 1);
                    } else {
                        // Skip the dot if its too small to click
                        continue;
                    }
                }

                var dotSize = pixelSize * dotScale;

                drawFlatPolygon(matrices, vertexConsumers, c -> {
                    c.accept(pos.x - dotSize, pos.y - dotSize);
                    c.accept(pos.x - dotSize, pos.y + dotSize);
                    c.accept(pos.x + dotSize, pos.y + dotSize);
                    c.accept(pos.x + dotSize, pos.y - dotSize);
                }, 0, (isDrawing && isLinked ? 0.5f : 1) * r, (isDrawing && isLinked ? 0.5f : 1) * g, 1 * b, 0.7f * alpha);
            }

            for (var line : patternList.entries()) {
                var first = getPatternDotPosition(x, y, line.p1(), patternSize);
                var second = getPatternDotPosition(x, y, line.p2(), patternSize);
                drawGlyphLine(matrices, vertexConsumers, first, second, pixelSize, isDrawing, 1, r, g, b, 0.7f * alpha);
            }

            if (inEditor && isDrawing) {
                var last = getPatternDotPosition(x, y, drawingPattern.getLast(), patternSize);
                var now = new Vector2f((float) mouseX, (float) mouseY);
                drawGlyphLine(matrices, vertexConsumers, last, now, pixelSize, true, 1, r, g, b, 0.7f * alpha);
            }
        } else {
            var textRenderer = MinecraftClient.getInstance().textRenderer;

//            var height = textRenderer.wrapLines(Text.literal(glyph.asString()), ) // TODO
            var text = glyph.asFormattedText();
            var height = 7;
            var width = textRenderer.getWidth(text);

            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(size / 1.3f / width, size / 1.3f / width, 1);

            var color = ColorHelper.Argb.withAlpha((int) (alpha * 0xff), 0xffffff);

            textRenderer.draw(
                    text,
                    -width / 2f, -height / 2f, color, false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                    0, 0xf000f0
            );

            matrices.pop();

            if (inEditor && inUI) {
                for (int i = 0; i < 9; i++) {
                    var pos = getPatternDotPosition(x, y, i, patternSize);

                    float dotScale;

                    if (isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(size)) {
                        dotScale = 1.6f;
                    } else {
                        if (isCircleClickable(size)) {
                            var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                            dotScale = Math.clamp(patternSize / (mouseDistance * 2) - 0.2f, 0, 1);
                        } else {
                            // Skip the dot if its too small to click
                            continue;
                        }
                    }

                    var dotSize = pixelSize * dotScale;

                    drawFlatPolygon(matrices, vertexConsumers, c -> {
                        c.accept(pos.x - dotSize, pos.y - dotSize);
                        c.accept(pos.x - dotSize, pos.y + dotSize);
                        c.accept(pos.x + dotSize, pos.y + dotSize);
                        c.accept(pos.x + dotSize, pos.y - dotSize);
                    }, 0, r, g, b, 0.25f);
                }
            }
        }
    }

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, boolean isDrawing, float tone, float r, float g, float b, float opacity) {
        var parallelVec = new Vector2f(last.y - now.y, now.x - last.x).normalize().mul(pixelSize / 2);
        var directionVec = new Vector2f(last.x - now.x, last.y - now.y).normalize().mul(pixelSize * 3);

        drawFlatPolygon(matrices, vertexConsumers, c -> {
            c.accept(last.x - parallelVec.x - directionVec.x, last.y - parallelVec.y - directionVec.y);
            c.accept(last.x + parallelVec.x - directionVec.x, last.y + parallelVec.y - directionVec.y);
            c.accept(now.x + parallelVec.x + directionVec.x, now.y + parallelVec.y + directionVec.y);
            c.accept(now.x - parallelVec.x + directionVec.x, now.y - parallelVec.y + directionVec.y);
        }, 0, (isDrawing ? 0.5f : tone) * r, (isDrawing ? 0.5f : tone) * g, tone * b, opacity);
    }

    public static Vector2f getPatternDotPosition(float x, float y, int i, float size) {
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

    public static boolean isInsideHitbox(Vector2f pos, float pixelSize, double mouseX, double mouseY) {
        var hitboxSize = CLICK_HITBOX_SIZE * pixelSize;
        return mouseX >= pos.x - hitboxSize && mouseX <= pos.x + hitboxSize &&
                mouseY >= pos.y - hitboxSize && mouseY <= pos.y + hitboxSize;
    }

    protected void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, float x1, float x2, float y1, float y2, float z, float r, float g, float b, float alpha, Vec3d normal) {
//        if (inUI) {
//            RenderSystem.setShaderTexture(0, texture);
//            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//            RenderSystem.enableBlend();
//            RenderSystem.enableDepthTest();
//            RenderSystem.setShaderColor(r, g, b, alpha);
//            Matrix4f position = matrices.peek().getPositionMatrix();
//            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
//            bufferBuilder.vertex(position, x1, y1, z).texture((float) 0, (float) 0);
//            bufferBuilder.vertex(position, x1, y2, z).texture((float) 0, (float) 1);
//            bufferBuilder.vertex(position, x2, y2, z).texture((float) 1, (float) 1);
//            bufferBuilder.vertex(position, x2, y1, z).texture((float) 1, (float) 0);
//            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
//            RenderSystem.setShaderColor(1, 1, 1, 1);
//        } else {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            var matrixEntry = matrices.peek();
            var position = matrixEntry.getPositionMatrix();
            var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
            vertexConsumer.vertex(position, x1, y1, z).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(matrixEntry, (float) normal.x, (float) normal.y, (float) normal.z);
            vertexConsumer.vertex(position, x1, y2, z).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(matrixEntry, (float) normal.x, (float) normal.y, (float) normal.z);
            vertexConsumer.vertex(position, x2, y2, z).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(matrixEntry, (float) normal.x, (float) normal.y, (float) normal.z);
            vertexConsumer.vertex(position, x2, y1, z).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(matrixEntry, (float) normal.x, (float) normal.y, (float) normal.z);
//        }
    }

    public static void drawFlatPolygon(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Consumer<BiConsumer<Float, Float>> vertexProvider, float z, float r, float g, float b, float alpha) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getGui());
        vertexProvider.accept((x, y) -> vertexConsumer.vertex(matrix4f, x, y, z).color(r, g, b, alpha));
    }
}

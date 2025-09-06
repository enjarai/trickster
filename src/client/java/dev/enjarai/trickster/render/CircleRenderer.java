package dev.enjarai.trickster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.fragment.FragmentRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import io.wispforest.owo.ui.core.Color;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.List;
import java.util.function.Function;

import static dev.enjarai.trickster.screen.SpellPartWidget.isCircleClickable;
import static net.minecraft.client.render.RenderPhase.*;

public class CircleRenderer {
    public static final Identifier CIRCLE_TEXTURE = Trickster.id("textures/gui/circle_48.png");
    public static final float PATTERN_TO_PART_RATIO = 2.5f;
    public static final int PART_PIXEL_RADIUS = 24;
    public static final int CLICK_HITBOX_SIZE = 5;

    public static final RenderLayer CIRCLE_TEXTURE_LAYER = RenderLayer.getEntityTranslucent(CIRCLE_TEXTURE);
    public static final RenderLayer GLYPH_LAYER = RenderLayer.of(
            "trickster:glyph", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS,
            786432, RenderLayer.MultiPhaseParameters.builder()
                    .program(GUI_PROGRAM)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .depthTest(LEQUAL_DEPTH_TEST)
                    .cull(DISABLE_CULLING)
                    .build(false)
    );

    public static final VertexConsumerProvider.Immediate VERTEX_CONSUMERS = VertexConsumerProvider.immediate(
            Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
                map.put(CIRCLE_TEXTURE_LAYER, new BufferAllocator(CIRCLE_TEXTURE_LAYER.getExpectedBufferSize()));
                map.put(GLYPH_LAYER, new BufferAllocator(GLYPH_LAYER.getExpectedBufferSize()));
            }),
            new BufferAllocator(2048)
    );

    public final boolean inUI;
    public final boolean animated;
    public final boolean inEditor;

    private double mouseX;
    private double mouseY;

    private float r = 1f, g = 1f, b = 1f;
    private float circleTransparency = 1f;

    public CircleRenderer(Boolean inUI, boolean inEditor) {
        this.inUI = inUI;
        this.inEditor = inEditor;
        this.animated = true;
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

    public boolean isInEditor() {
        return inEditor;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public void setCircleTransparency(float circleTransparency) {
        this.circleTransparency = circleTransparency;
    }

    public float getCircleTransparency() {
        return circleTransparency;
    }

    public void renderCircle(MatrixStack matrices, SpellPart entry, double x, double y, double radius, double startingAngle, float delta,
            Function<Double, Double> alphaGetter, Vec3d normal, @Nullable List<Byte> drawingPattern) {
        var vertexConsumers = VERTEX_CONSUMERS;
        var alpha = alphaGetter.apply(radius);

        drawTexturedQuad(
                matrices, vertexConsumers, CIRCLE_TEXTURE,
                (float) (x - radius), (float) (x + radius), (float) (y - radius), (float) (y + radius),
                0,
                r, g, b, (float) (alpha * circleTransparency), inUI
        );
        if (!(entry.glyph instanceof SpellPart)) {
            matrices.push();
            drawSide(matrices, vertexConsumers, (float) x, (float) y, (float) radius, alphaGetter, normal, delta, entry.glyph, drawingPattern);
            matrices.pop();
        }

        int partCount = entry.partCount();

        drawDivider(matrices, vertexConsumers, x, y, startingAngle, radius, partCount, alpha);
    }

    protected void drawDivider(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double x, double y, double startingAngle, double radius, float partCount, double alpha) {
        var pixelSize = radius / PART_PIXEL_RADIUS;
        var lineAngle = startingAngle + (2 * Math.PI) / partCount * -0.5 - (Math.PI / 2);

        float lineX = (float) (x + (radius * Math.cos(lineAngle)));
        float lineY = (float) (y + (radius * Math.sin(lineAngle)));

        var toCenterVec = new Vector2f((float) (lineX - x), (float) (lineY - y)).normalize();
        var perpendicularVec = new Vector2f(toCenterVec).perpendicular();
        toCenterVec.mul((float) (pixelSize * 6));
        perpendicularVec.mul((float) (pixelSize * 0.5f));

        Color dividerColor = Trickster.CONFIG.subcircleDividerPinColor();

        drawFlatPolygon(matrices, vertexConsumers,
                lineX - perpendicularVec.x + toCenterVec.x * 0.5f, lineY - perpendicularVec.y + toCenterVec.y * 0.5f,
                lineX + perpendicularVec.x + toCenterVec.x * 0.5f, lineY + perpendicularVec.y + toCenterVec.y * 0.5f,
                lineX + perpendicularVec.x - toCenterVec.x, lineY + perpendicularVec.y - toCenterVec.y,
                lineX - perpendicularVec.x - toCenterVec.x, lineY - perpendicularVec.y - toCenterVec.y,
                0, dividerColor.red() * r, dividerColor.green() * g, dividerColor.blue() * b, dividerColor.alpha() * (float) alpha);
    }

    private void drawSide(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y,
            float radius, Function<Double, Double> alphaGetter, Vec3d normal, float delta,
            Fragment glyph, @Nullable List<Byte> drawingPattern) {
        float alpha = (float) (double) alphaGetter.apply((double) radius);
        float patternRadius = radius / PATTERN_TO_PART_RATIO;
        float pixelSize = patternRadius / PART_PIXEL_RADIUS;

        var isDrawing = drawingPattern != null;

        if (glyph instanceof PatternGlyph || isDrawing) {
            var patternList = isDrawing ? Pattern.from(drawingPattern) : ((PatternGlyph) glyph).pattern();

            for (int i = 0; i < 9; i++) {
                var pos = getPatternDotPosition(x, y, i, patternRadius);
                var isLinked = isDrawing ? drawingPattern.contains((byte) i) : patternList.contains(i);
                float dotScale = 1;

                if (isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(radius)) {
                    dotScale = 1.6f;
                } else if (!isLinked) {
                    if (isCircleClickable(radius)) {
                        var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                        dotScale = Math.clamp(patternRadius / mouseDistance - 0.2f, 0, 1);
                    } else {
                        // Skip the dot if its too small to click
                        continue;
                    }
                }

                float dotSize = pixelSize * dotScale;

                drawFlatPolygon(matrices, vertexConsumers,
                        pos.x - dotSize, pos.y - dotSize,
                        pos.x - dotSize, pos.y + dotSize,
                        pos.x + dotSize, pos.y + dotSize,
                        pos.x + dotSize, pos.y - dotSize,
                        0, (isDrawing && isLinked ? 0.8f : 1) * r, (isDrawing && isLinked ? 0.5f : 1) * g, 1 * b, 0.7f * alpha);
            }

            for (var line : patternList.entries()) {
                var first = getPatternDotPosition(x, y, line.p1(), patternRadius);
                var second = getPatternDotPosition(x, y, line.p2(), patternRadius);
                drawGlyphLine(matrices, vertexConsumers, first, second, pixelSize, isDrawing, 1, r, g, b, 0.7f * alpha, animated && inUI);
            }

            if (isDrawing) {
                var last = getPatternDotPosition(x, y, drawingPattern.getLast(), patternRadius);
                var now = new Vector2f((float) mouseX, (float) mouseY);
                drawGlyphLine(matrices, vertexConsumers, last, now, pixelSize, true, 1, r, g, b, 0.7f * alpha, animated && inUI);
            }
        } else {
            //noinspection rawtypes
            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(glyph.type()));

            var renderDots = true;

            if (renderer != null) {
                //noinspection unchecked
                renderer.render(glyph, matrices, vertexConsumers, x, y, radius, alpha, normal, delta, this);
                renderDots = renderer.renderRedrawDots();
            } else {
                FragmentRenderer.renderAsText(glyph, matrices, vertexConsumers, x, y, radius, alpha);
            }

            if (inUI && renderDots) {
                for (int i = 0; i < 9; i++) {
                    var pos = getPatternDotPosition(x, y, i, patternRadius);

                    float dotScale;

                    if (isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(radius)) {
                        dotScale = 1.6f;
                    } else {
                        if (isCircleClickable(radius)) {
                            var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                            dotScale = Math.clamp(patternRadius / (mouseDistance * 2) - 0.2f, 0, 1);
                        } else {
                            // Skip the dot if its too small to click
                            continue;
                        }
                    }

                    var dotSize = pixelSize * dotScale;

                    drawFlatPolygon(matrices, vertexConsumers,
                            pos.x - dotSize, pos.y - dotSize,
                            pos.x - dotSize, pos.y + dotSize,
                            pos.x + dotSize, pos.y + dotSize,
                            pos.x + dotSize, pos.y - dotSize,
                            0, r, g, b, 0.25f);
                }
            }
        }
    }

    private static final Random glyphRandom = new LocalRandom(0);

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, boolean isDrawing, float tone, float r, float g,
            float b, float opacity, boolean animated) {
        if (last.distance(now) < pixelSize * 6) {
            return;
        }

        var parallelVec = new Vector2f(last.y - now.y, now.x - last.x).normalize().mul(pixelSize / 2);
        var directionVec = new Vector2f(last.x - now.x, last.y - now.y).normalize().mul(pixelSize * 3);

        if (animated) {
            var lineStart = new Vector2f(last.x - directionVec.x, last.y - directionVec.y);
            var lineEnd = new Vector2f(now.x + directionVec.x, now.y + directionVec.y);

            var parallel1 = parallelVec.mul(1 + glyphRandom.nextFloat() - 0.5f, new Vector2f());
            var parallel2 = new Vector2f();

            float steps = last.distance(now) / pixelSize / 4;
            for (int i = 0; i < steps; i++) {
                var lineLength = Math.min(1, steps - i);

                var pos1 = lineStart.lerp(lineEnd, i / steps, new Vector2f());
                var pos2 = lineStart.lerp(lineEnd, (i + lineLength) / steps, new Vector2f());

                parallel2 = parallelVec.mul(1 + (glyphRandom.nextFloat() - 0.5f) * lineLength, parallel2);

                Vector2f finalParallel1 = parallel1;
                Vector2f finalParallel2 = parallel2;
                drawFlatPolygon(matrices, vertexConsumers,
                        pos1.x - finalParallel1.x, pos1.y - finalParallel1.y,
                        pos1.x + finalParallel1.x, pos1.y + finalParallel1.y,
                        pos2.x + finalParallel2.x, pos2.y + finalParallel2.y,
                        pos2.x - finalParallel2.x, pos2.y - finalParallel2.y,
                        0, (isDrawing ? 0.8f : tone) * r, (isDrawing ? 0.5f : tone) * g, 1 * b, opacity);

                parallel1 = parallel1.set(parallel2);
            }
        } else {
            drawFlatPolygon(matrices, vertexConsumers,
                    last.x - parallelVec.x - directionVec.x, last.y - parallelVec.y - directionVec.y,
                    last.x + parallelVec.x - directionVec.x, last.y + parallelVec.y - directionVec.y,
                    now.x + parallelVec.x + directionVec.x, now.y + parallelVec.y + directionVec.y,
                    now.x - parallelVec.x + directionVec.x, now.y - parallelVec.y + directionVec.y,
                    0, (isDrawing ? 0.5f : tone) * r, (isDrawing ? 0.5f : tone) * g, tone * b, opacity);
        }
    }

    public static Vector2f getPatternDotPosition(float x, float y, int i, float radius) {
        float xSign = (float) (i % 3 - 1);
        float ySign = (float) (i / 3 - 1);

        if (xSign != 0 && ySign != 0) {
            xSign *= 0.7f;
            ySign *= 0.7f;
        }

        return new Vector2f(
                x + xSign * radius,
                y + ySign * radius
        );
    }

    public static boolean isInsideHitbox(Vector2f pos, float pixelSize, double mouseX, double mouseY) {
        var hitboxSize = CLICK_HITBOX_SIZE * pixelSize;
        return mouseX >= pos.x - hitboxSize && mouseX <= pos.x + hitboxSize &&
                mouseY >= pos.y - hitboxSize && mouseY <= pos.y + hitboxSize;
    }

    public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, float x1, float x2, float y1, float y2, float z, float r, float g, float b,
            float alpha, boolean inGui) {
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

        if (!inGui) {
            vertexConsumer.vertex(position, x1, y1, z).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x1, y2, z).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x2, y2, z).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x2, y1, z).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
        } else {
            vertexConsumer.vertex(position, x1, y1, z).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x1, y2, z).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x2, y2, z).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x2, y1, z).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
        }
    }

    public static void drawFlatPolygon(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            float z, float r, float g, float b, float alpha) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GLYPH_LAYER);
        vertexConsumer.vertex(matrix4f, x1, y1, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x2, y2, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x3, y3, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x4, y4, z).color(r, g, b, alpha);
    }
}

package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;
import static dev.enjarai.trickster.screen.SpellPartWidget.isCircleClickable;

public class PatternRenderer implements FragmentRenderer<PatternGlyph> {
    @Override
    public void render(PatternGlyph fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
            SpellCircleRenderer delegator) {
        renderPattern(fragment.pattern(), matrices, vertexConsumers, x, y, size / PATTERN_TO_PART_RATIO, alpha, delegator);
    }

    public static void renderPattern(Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, SpellCircleRenderer delegator) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, 0.5f, 3f, true, alpha, delegator);
    }

    public static void renderPattern(Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float width, float height, boolean renderDots,
            float alpha,
            SpellCircleRenderer delegator) {
        var pixelSize = size / PART_PIXEL_RADIUS;

        var r = delegator.getR();
        var g = delegator.getG();
        var b = delegator.getB();

        // Duplicate code :brombeere:
        if (renderDots)
            for (int i = 0; i < 9; i++) {
                var pos = getPatternDotPosition(x, y, i, size);

                var isLinked = pattern.contains(i);
                float dotScale = 1;

                if (delegator.isInEditor() && isInsideHitbox(pos, pixelSize, delegator.getMouseX(), delegator.getMouseY()) && isCircleClickable(size)) {
                    dotScale = 1.6f;
                } else if (!isLinked) {
                    if (delegator.isInEditor() && isCircleClickable(size)) {
                        var mouseDistance = new Vector2f((float) (delegator.getMouseX() - pos.x), (float) (delegator.getMouseY() - pos.y)).length();
                        dotScale = Math.clamp(size / mouseDistance - 0.2f, 0, 1);
                    } else {
                        // Skip the dot if it's too small to click
                        continue;
                    }
                }

                var dotSize = pixelSize * dotScale;

                drawFlatPolygon(matrices, vertexConsumers,
                        pos.x - dotSize, pos.y - dotSize,
                        pos.x - dotSize, pos.y + dotSize,
                        pos.x + dotSize, pos.y + dotSize,
                        pos.x + dotSize, pos.y - dotSize,
                        0, r, g, b, 0.7f * alpha);
            }

        for (var line : pattern.entries()) {
            var first = getPatternDotPosition(x, y, line.p1(), size);
            var second = getPatternDotPosition(x, y, line.p2(), size);
            drawGlyphLine(matrices, vertexConsumers, first, second, pixelSize, width, height, false, 1, r, g, b, 0.7f * alpha, delegator.animated);
        }
    }

    @Override
    public boolean renderRedrawDots() {
        return false;
    }
}

package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class PatternRenderer implements FragmentRenderer<PatternGlyph> {
    @Override
    public void render(PatternGlyph fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha) {
        renderPattern(fragment.pattern(), matrices, vertexConsumers, x, y, size, alpha);
    }

    public static void renderPattern(Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha) {
        var pixelSize = size / PART_PIXEL_RADIUS;

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x, y, i, size);

            var isLinked = pattern.contains(i);
            var dotSize = pixelSize;

            drawFlatPolygon(matrices, vertexConsumers, c -> {
                c.accept(pos.x - dotSize, pos.y - dotSize);
                c.accept(pos.x - dotSize, pos.y + dotSize);
                c.accept(pos.x + dotSize, pos.y + dotSize);
                c.accept(pos.x + dotSize, pos.y - dotSize);
            }, 0, 0, 0, 0, isLinked ? 0.9f : 0.5f);
        }

        for (var line : pattern.entries()) {
            var now = getPatternDotPosition(x, y, line.p1(), size);
            var last = getPatternDotPosition(x, y, line.p2(), size);
            drawGlyphLine(matrices, vertexConsumers, last, now, 1, false, 0, 1f, 1f, 1f, alpha);
        }
    }
}

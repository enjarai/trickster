package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static dev.enjarai.trickster.render.CircleRenderer.GLYPH_LAYER;

public class MapRenderer implements FragmentRenderer<MapFragment> {
    private final static float HORIZONTAL_SPACING = 0.6f;

    @Override
    public void render(MapFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var map = fragment.map();
        var layout = calculateLayout(fragment);
        var height = layout.height();
        var width = layout.width();
        var elementWidth = layout.elementWidth();
        var scale = layout.scale();

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius * 2, radius * 2, 1);
        matrices.scale(scale, scale, 1);

        matrices.push();
        matrices.translate(0, -0.5 * height, 0);

        var offsetAcc = 0.0f;
        for (var entry : map) {
            var entryHeight = Math.max(FragmentRenderer.getFragmentProportionalHeight(entry._1()), FragmentRenderer.getFragmentProportionalHeight(entry._2()));
            var offset = offsetAcc + entryHeight / 2;

            var key = entry._1();
            var val = entry._2();

            FragmentRenderer keyRenderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(key.type()));
            if (keyRenderer != null) {
                keyRenderer.render(key, matrices, vertexConsumers, -(elementWidth + HORIZONTAL_SPACING) * 0.5f, offset, 0.4f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(key, matrices, vertexConsumers, -(elementWidth + HORIZONTAL_SPACING) * 0.5f, offset, 0.4f, alpha);
            }

            renderArrow(matrices, vertexConsumers, 0, offset, 0.02f, alpha);

            FragmentRenderer valRenderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(val.type()));
            if (valRenderer != null) {
                valRenderer.render(val, matrices, vertexConsumers, (elementWidth + HORIZONTAL_SPACING) * 0.5f, offset, 0.4f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(val, matrices, vertexConsumers, (elementWidth + HORIZONTAL_SPACING) * 0.5f, offset, 0.4f, alpha);
            }

            offsetAcc += entryHeight;
        }

        matrices.pop();

        var bracketHeight = Math.max(height + 0.15f, 0.5f);
        renderBrace(matrices, vertexConsumers, 0, width / 2f, 0, bracketHeight, alpha);
        renderBrace(matrices, vertexConsumers, (float) Math.PI, -width / 2f, 0, bracketHeight, alpha);

        matrices.pop();
    }

    private void renderArrow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha) {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var text = Text.literal("->");
        var height = 7;
        var width = textRenderer.getWidth(text);

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius, radius, 1);

        var color = ColorHelper.Argb.withAlpha((int) (alpha * 0xff), 0xffffff);

        textRenderer.draw(
            text,
            -(width - 1f) / 2f, -height / 2f, color, false,
            matrices.peek().getPositionMatrix(),
            vertexConsumers, TextRenderer.TextLayerType.NORMAL,
            0, 0xf000f0
        );

        matrices.pop();
    }

    private void renderBrace(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, float x, float y, float height, float alpha) {
        float lineWidth = 0.1f;
        height = Math.max(lineWidth * 7, height - height % lineWidth);

        float top = -height * 0.5f;
        float bottom = height * 0.5f;
        float left = -lineWidth;
        float right = 0.0f;

        matrices.push();

        matrices.translate(x, y, 0);
        matrices.multiply(new Quaternionf().rotateZ(rotation));

        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer vc = vertexConsumers.getBuffer(GLYPH_LAYER);

        // top
        vc.vertex(m, left - lineWidth * 2f, top + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, top + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth * 2f, top, 0).color(1f, 1f, 1f, alpha);

        // upper spine
        vc.vertex(m, left - lineWidth, lineWidth * -0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right - lineWidth, lineWidth * -0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right - lineWidth, top + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, top + lineWidth, 0).color(1f, 1f, 1f, alpha);

        // middle
        vc.vertex(m, right - lineWidth, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right - lineWidth, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);

        // lower spine
        vc.vertex(m, left - lineWidth, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right - lineWidth, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right - lineWidth, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);

        // bottom
        vc.vertex(m, left - lineWidth * 2f, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left - lineWidth * 2f, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);

        matrices.pop();
    }

    @Override
    public float getProportionalHeight(MapFragment fragment) {
        var layout = calculateLayout(fragment);
        return layout.height() * layout.scale();
    }

    @Override
    public float getProportionalWidth(MapFragment fragment) {
        var layout = calculateLayout(fragment);
        return (layout.elementWidth * 2 + HORIZONTAL_SPACING * 3) * layout.scale();
    }

    private static Layout calculateLayout(MapFragment fragment) {
        var map = fragment.map();

        float height = 0.0f;
        float maxElementWidth = 0.0f;
        for (var entry : map) {
            height += Math.max(FragmentRenderer.getFragmentProportionalHeight(entry._1()), FragmentRenderer.getFragmentProportionalHeight(entry._2()));
            var width = Math.max(FragmentRenderer.getFragmentProportionalWidth(entry._1()), FragmentRenderer.getFragmentProportionalWidth(entry._2()));
            maxElementWidth = Math.max(maxElementWidth, width);
        }

        float width = maxElementWidth * 2 + HORIZONTAL_SPACING * 2;
        float diagonal = (float) Math.sqrt(width * width + height * height);

        float scale = 0.45f / diagonal;
        return new Layout(height, width, maxElementWidth, scale);
    }

    private record Layout(float height, float width, float elementWidth, float scale) {
    }
}

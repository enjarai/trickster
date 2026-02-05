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
    @Override
    public void render(MapFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var map = fragment.map();
        var spacing = 0.1f;
        var height = 0.0f;
        for (var entry : map) {
            height += Math.max(FragmentRenderer.get_fragment_proportional_height(entry._1()), FragmentRenderer.get_fragment_proportional_height(entry._2())) + spacing;
        }

        var scale = Math.min(0.3f, 1.0f / height);

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius, radius, 1);
        matrices.scale(scale, scale, 1);

        matrices.push();
        matrices.translate(0, -0.5 * height, 0);

        var offset_acc = 0.0f;
        for (var entry : map) {
            var entry_height = Math.max(FragmentRenderer.get_fragment_proportional_height(entry._1()), FragmentRenderer.get_fragment_proportional_height(entry._2()));
            var offset = offset_acc + (spacing + entry_height) / 2;

            var key = entry._1();
            var val = entry._2;

            FragmentRenderer key_renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(key.type()));
            if (key_renderer != null) {
                key_renderer.render(key, matrices, vertexConsumers, -0.8f, offset, 1.0f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(key, matrices, vertexConsumers, -0.8f, offset, 1.0f, alpha);
            }

            render_arrow(matrices, vertexConsumers, 0, offset, 0.04f, alpha);

            FragmentRenderer val_renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(val.type()));
            if (val_renderer != null) {
                val_renderer.render(val, matrices, vertexConsumers, 0.8f, offset, 1.0f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(val, matrices, vertexConsumers, 0.8f, offset, 1.0f, alpha);
            }

            offset_acc += (spacing + entry_height);
        }

        matrices.pop();

        var bracket_height = Math.max(height + 0.15f, 0.5f);
        render_brace(matrices, vertexConsumers, 0, 1.4f, 0, bracket_height, alpha);
        render_brace(matrices, vertexConsumers, (float) Math.PI, -1.4f, 0, bracket_height, alpha);

        matrices.pop();
    }

    private void render_arrow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha) {
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

    private void render_brace(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, float x, float y, float height, float alpha) {
        float lineWidth = 0.1f;

        float top = height * 0.5f;
        float bottom = -height * 0.5f;
        float left = -lineWidth * 0.5f;
        float right = lineWidth * 0.5f;

        matrices.push();

        matrices.translate(x, y, 0);
        matrices.multiply(new Quaternionf().rotateZ(rotation));

        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer vc = vertexConsumers.getBuffer(GLYPH_LAYER);

        // upper spine
        vc.vertex(m, left, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, top - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left, top - lineWidth, 0).color(1f, 1f, 1f, alpha);

        // lower spine
        vc.vertex(m, left, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);

        vc.vertex(m, right, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right + lineWidth, lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right + lineWidth, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, -lineWidth * 0.5f, 0).color(1f, 1f, 1f, alpha);

        // top
        vc.vertex(m, -lineWidth * 1.5f, top - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, top - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 1.5f, top, 0).color(1f, 1f, 1f, alpha);

        // bottom
        vc.vertex(m, -lineWidth * 1.5f, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 1.5f, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);

        matrices.pop();
    }

    @Override
    public float get_proportional_height(MapFragment fragment) {
        return 0;
    }
}

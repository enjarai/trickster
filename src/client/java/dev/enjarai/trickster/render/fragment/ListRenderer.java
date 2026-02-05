package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static dev.enjarai.trickster.render.CircleRenderer.GLYPH_LAYER;

public class ListRenderer implements FragmentRenderer<ListFragment> {
    @Override
    public void render(ListFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var fragments = fragment.fragments();
        var spacing = 0.1f;
        var height = 0.0f;
        for (Fragment element : fragments) {
            height += FragmentRenderer.get_fragment_proportional_height(element) + spacing;
        }

        var scale = Math.min(0.6f, 1.0f / height);

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius, radius, 1);
        matrices.scale(scale, scale, 1);

        matrices.push();
        matrices.translate(0, -0.5 * height, 0);

        var offset_acc = 0.0f;
        for (Fragment element : fragments) {
            var element_height = FragmentRenderer.get_fragment_proportional_height(element);
            var offset = offset_acc + (spacing + element_height) / 2;

            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(element.type()));
            if (renderer != null) {
                renderer.render(element, matrices, vertexConsumers, 0, offset, 1.0f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(element, matrices, vertexConsumers, 0, offset, 1.0f, alpha);
            }

            offset_acc += (spacing + element_height);
        }

        matrices.pop();

        var bracket_height = Math.max(height + 0.15f, 0.5f);
        render_bracket(matrices, vertexConsumers, 0, 0.6f, 0, bracket_height, alpha);
        render_bracket(matrices, vertexConsumers, (float) Math.PI, -0.6f, 0, bracket_height, alpha);

        matrices.pop();

    }

    private void render_bracket(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, float x, float y, float height, float alpha) {
        float lineWidth = 0.1f;
        float legLength = 0.2f;

        float top = height * 0.5f;
        float bottom = -height * 0.5f;
        float left = -lineWidth * 0.5f;
        float right = lineWidth * 0.5f;

        matrices.push();

        matrices.translate(x, y, 0);
        matrices.multiply(new Quaternionf().rotateZ(rotation));

        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer vc = vertexConsumers.getBuffer(GLYPH_LAYER);

        // spine
        vc.vertex(m, left, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left, top, 0).color(1f, 1f, 1f, alpha);

        // top leg
        vc.vertex(m, -legLength, top - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, top - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -legLength, top, 0).color(1f, 1f, 1f, alpha);

        // bottom leg
        vc.vertex(m, -legLength, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth * 0.5f, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -legLength, bottom + lineWidth, 0).color(1f, 1f, 1f, alpha);

        matrices.pop();
    }

    @Override
    public float get_proportional_height(ListFragment fragment) {
        return 1.0f;
    }
}

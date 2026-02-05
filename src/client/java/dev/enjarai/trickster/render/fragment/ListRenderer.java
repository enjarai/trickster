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
    private static float SPACING = 0.1f;

    @Override
    public void render(ListFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var fragments = fragment.fragments();
        var layout = calculateLayout(fragment);
        var height = layout.height();
        var scale = layout.scale();

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius, radius, 1);
        matrices.scale(scale, scale, 1);

        matrices.push();
        matrices.translate(0, -0.5 * height, 0);

        var offsetAcc = 0.0f;
        for (Fragment element : fragments) {
            var elementHeight = FragmentRenderer.getFragmentProportionalHeight(element);
            var offset = offsetAcc + (SPACING + elementHeight) / 2;

            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(element.type()));
            if (renderer != null) {
                renderer.render(element, matrices, vertexConsumers, 0, offset, 1.0f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(element, matrices, vertexConsumers, 0, offset, 1.0f, alpha);
            }

            offsetAcc += (SPACING + elementHeight);
        }

        matrices.pop();

        var bracketHeight = Math.max(height + 0.15f, 0.5f);
        renderBracket(matrices, vertexConsumers, 0, 1f, 0, bracketHeight, alpha);
        renderBracket(matrices, vertexConsumers, (float) Math.PI, -1f, 0, bracketHeight, alpha);

        matrices.pop();

    }

    private void renderBracket(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, float x, float y, float height, float alpha) {
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
    public float getProportionalHeight(ListFragment fragment) {
        var layout = calculateLayout(fragment);
        return layout.height() * layout.scale();
    }

    private static Layout calculateLayout(ListFragment fragment) {
        var fragments = fragment.fragments();

        float height = 0.0f;

        for (Fragment element : fragments) {
            height += FragmentRenderer.getFragmentProportionalHeight(element) + SPACING;
        }

        float scale = Math.min(0.6f, 1.0f / height);
        return new Layout(height, scale);
    }

    private record Layout(float height, float scale) {
    }
}

package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.*;
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
        var layout = calculateLayout(fragment);
        var height = layout.height();
        var width = layout.width();
        var scale = layout.scale();

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius * 2, radius * 2, 1);
        matrices.scale(scale, scale, 1);

        matrices.push();
        matrices.translate(0, -0.5 * height, 0);

        var offsetAcc = 0.0f;
        for (Fragment element : fragments) {
            var elementHeight = FragmentRenderer.getFragmentProportionalHeight(element);
            var offset = offsetAcc + elementHeight / 2;

            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(element.type()));
            if (renderer != null) {
                renderer.render(element, matrices, vertexConsumers, 0, offset, 0.4f, alpha, normal, tickDelta, delegator);
            } else {
                FragmentRenderer.renderAsText(element, matrices, vertexConsumers, 0, offset, 0.4f, alpha);
            }

            offsetAcc += elementHeight;
        }

        matrices.pop();

        float lineWidth = 0.025f;
        float bracketHeight = Math.max(height, 0.1f);
        renderBracket(matrices, vertexConsumers, 0, width * 0.5f, 0, bracketHeight, alpha, lineWidth);
        renderBracket(matrices, vertexConsumers, (float) Math.PI, -width * 0.5f, 0, bracketHeight, alpha, lineWidth);

        matrices.pop();
    }

    private void renderBracket(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, float x, float y, float height, float alpha, float lineWidth) {
        float legLength = 2 * lineWidth;

        float top = -height * 0.5f;
        float bottom = height * 0.5f;
        float left = -lineWidth;
        float right = 0.0f;

        matrices.push();

        matrices.translate(x, y, 0);
        matrices.multiply(new Quaternionf().rotateZ(rotation));

        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer vc = vertexConsumers.getBuffer(GLYPH_LAYER);

        // top leg
        vc.vertex(m, -legLength - lineWidth, top + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth, top + lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -legLength - lineWidth, top, 0).color(1f, 1f, 1f, alpha);

        // spine
        vc.vertex(m, left, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, right, top, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, left, top, 0).color(1f, 1f, 1f, alpha);

        // bottom leg
        vc.vertex(m, -legLength - lineWidth, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth, bottom, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -lineWidth, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);
        vc.vertex(m, -legLength - lineWidth, bottom - lineWidth, 0).color(1f, 1f, 1f, alpha);

        matrices.pop();
    }

    @Override
    public float getProportionalHeight(ListFragment fragment) {
        var layout = calculateLayout(fragment);
        return layout.height() * layout.scale();
    }

    @Override
    public float getProportionalWidth(ListFragment fragment) {
        var layout = calculateLayout(fragment);
        return layout.width() * layout.scale();
    }

    private static Layout calculateLayout(ListFragment fragment) {
        var fragments = fragment.fragments();

        float height = 0.0f;
        float maxElementWidth = 0.0f;
        for (Fragment element : fragments) {
            maxElementWidth = Math.max(maxElementWidth, FragmentRenderer.getFragmentProportionalWidth(element));
            height += FragmentRenderer.getFragmentProportionalHeight(element);
        }
        float width = maxElementWidth + 0.1f;

        float diagonal = (float) Math.sqrt(width * width + height * height);

        float scale = 0.45f / diagonal;
        return new Layout(height, width, scale);
    }

    private record Layout(float height, float width, float scale) {
    }
}

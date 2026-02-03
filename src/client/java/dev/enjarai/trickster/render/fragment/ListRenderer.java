package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class ListRenderer implements FragmentRenderer<ListFragment> {
    @Override
    public void render(ListFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var texts = fragment.fragments().stream().map(Fragment::asFormattedText).toList();

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius / 20, radius / 20, 1);

        var color = ColorHelper.Argb.withAlpha((int) (alpha * 0xff), 0xffffff);

        textRenderer.draw(
            "[",
            -14, -3.5f, color, false,
            matrices.peek().getPositionMatrix(),
            vertexConsumers, TextRenderer.TextLayerType.NORMAL,
            0, 0xf000f0
        );

        textRenderer.draw(
            "]",
            11, -3.5f, color, false,
            matrices.peek().getPositionMatrix(),
            vertexConsumers, TextRenderer.TextLayerType.NORMAL,
            0, 0xf000f0
        );

        matrices.pop();

        var i = 0;

        var maxWidth = 1;
        for (var text : texts) {
            var size = textRenderer.getWidth(text);
            if (size > maxWidth) {
                maxWidth = size;
            }
        }
        var maxHeight = 10 * texts.size();
        var maxSize = Math.max(maxWidth, maxHeight);

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(radius / maxSize, radius / maxSize, 1);

        for (var text : texts) {
            var width = textRenderer.getWidth(text);

            textRenderer.draw(
                text,
                -(width - 1f) / 2f, -maxHeight / 2f + i * 10f, 0xffffffff, false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0, 0xf000f0
            );

            i++;
        }

        matrices.pop();
    }
}

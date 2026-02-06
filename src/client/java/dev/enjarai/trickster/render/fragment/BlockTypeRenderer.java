package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

public class BlockTypeRenderer implements FragmentRenderer<BlockTypeFragment> {
    static float HEIGHT = 0.4f;

    @Override
    public void render(BlockTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var block = fragment.block();

        if (Item.BLOCK_ITEMS.containsKey(block)) {
            var stack = Item.BLOCK_ITEMS.get(block).getDefaultStack();
            ItemTypeRenderer.renderItem(stack, ModelTransformationMode.NONE, matrices, vertexConsumers, x, y, size, delegator, 14, true);
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
        }
    }

    @Override
    public boolean doubleSided() {
        return false;
    }

    @Override
    public float getProportionalHeight(BlockTypeFragment fragment) {
        var block = fragment.block();

        if (Item.BLOCK_ITEMS.containsKey(block)) {
            return HEIGHT;
        } else {
            return FragmentRenderer.getTextProportionalHeight(fragment.asFormattedText());
        }
    }

    @Override
    public float getProportionalWidth(BlockTypeFragment fragment) {
        var block = fragment.block();

        if (Item.BLOCK_ITEMS.containsKey(block)) {
            return HEIGHT;
        } else {
            return FragmentRenderer.getTextProportionalWidth();
        }
    }
}

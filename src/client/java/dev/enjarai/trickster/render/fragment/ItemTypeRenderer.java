package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ItemTypeRenderer implements FragmentRenderer<ItemTypeFragment> {
    @Override
    public void render(ItemTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, SpellCircleRenderer delegator) {
        var stack = fragment.item().getDefaultStack();
        var bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(
                stack, MinecraftClient.getInstance().world,
                null, 0
        );

        matrices.push();

        matrices.translate(x, y, 0);
        matrices.scale(size, delegator.inUI ? -size : size, delegator.inUI ? size : size * 0.01f);
        matrices.scale(0.8f, 0.8f, 0.8f);
        if (!delegator.inUI) {
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180));
        }

        boolean notSideLit = !bakedModel.isSideLit() && delegator.inUI;
        if (notSideLit) {
            if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
                immediate.draw();
            }

            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack, ModelTransformationMode.GUI,
                false, matrices, vertexConsumers,
                LightmapTextureManager.pack(0, 14), OverlayTexture.DEFAULT_UV,
                bakedModel
        );
        if (delegator.inUI && vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }

        if (notSideLit) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
    }

    @Override
    public boolean drawTwoSides() {
        return false;
    }
}

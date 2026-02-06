package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ItemTypeRenderer implements FragmentRenderer<ItemTypeFragment> {
    static float HEIGHT = 0.4f;

    @Override
    public void render(ItemTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var stack = fragment.item().getDefaultStack();
        renderItem(stack, ModelTransformationMode.GUI, matrices, vertexConsumers, x, y, size, delegator, 14, false);
    }

    public static void renderItem(ItemStack stack, ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size,
        CircleRenderer delegator, int light, boolean alwaysFlatLight) {
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

        boolean notSideLit = (alwaysFlatLight || !bakedModel.isSideLit()) && delegator.inUI;
        if (notSideLit) {
            if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
                immediate.draw();
            }

            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(
            stack, transformationMode,
            false, matrices, vertexConsumers,
            LightmapTextureManager.pack(0, light), OverlayTexture.DEFAULT_UV,
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
    public boolean doubleSided() {
        return false;
    }

    @Override
    public float getProportionalHeight(ItemTypeFragment fragment) {
        return HEIGHT;
    }
}

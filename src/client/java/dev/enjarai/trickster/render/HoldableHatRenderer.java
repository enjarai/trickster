package dev.enjarai.trickster.render;

import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.Side;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class HoldableHatRenderer implements AccessoryRenderer {
    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, MatrixStack matrices, EntityModel<M> model, VertexConsumerProvider multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof BipedEntityModel<? extends LivingEntity> humanoidModel)) return;

        AccessoryRenderer.transformToFace(matrices, humanoidModel.head, Side.TOP);
        matrices.scale(1.25f, 1.25f, 1.25f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrices.translate(0.0, -0.4, 0.01);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack, ModelTransformationMode.HEAD, light, OverlayTexture.DEFAULT_UV,
                matrices, multiBufferSource, reference.entity().getWorld(), 0
        );
    }
}

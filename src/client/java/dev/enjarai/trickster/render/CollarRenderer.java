package dev.enjarai.trickster.render;

import com.google.common.base.Suppliers;
import dev.enjarai.trickster.Trickster;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

// Partially adapted from Collar Trinkets by wiidotmom (https://github.com/wiidotmom/collar-trinkets)
public class CollarRenderer implements AccessoryRenderer {
    private static final Identifier TEXTURE = Trickster.id("textures/entity/collar.png");
    private static final Supplier<BipedEntityModel<LivingEntity>> MODEL = Suppliers.memoize(() ->
            new Model(Model.createTexturedModelData().createModel()));

    @Override
    public <M extends LivingEntity> void render(ItemStack itemStack, SlotReference slotReference, MatrixStack matrixStack, EntityModel<M> entityModel, VertexConsumerProvider vertexConsumerProvider, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BipedEntityModel<LivingEntity> model = MODEL.get();
        model.setAngles(slotReference.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.animateModel(slotReference.entity(), limbSwing, limbSwingAmount, ageInTicks);
        followBodyRotations(slotReference.entity(), model);
        VertexConsumer consumer = vertexConsumerProvider.getBuffer(model.getLayer(TEXTURE));

        model.render(matrixStack, consumer, light, OverlayTexture.DEFAULT_UV);

//        BipedEntityModel<LivingEntity> bellModel = BELL_MODEL.get();
//        bellModel.setAngles(slotReference.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//        bellModel.animateModel(slotReference.entity(), limbSwing, limbSwingAmount, ageInTicks);
//        followBodyRotations(slotReference.entity(), bellModel);
//        VertexConsumer bellConsumer = vertexConsumerProvider.getBuffer(bellModel.getLayer(TEXTURE));
//        bellModel.render(matrixStack, bellConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void followBodyRotations(LivingEntity entity, BipedEntityModel<LivingEntity> model) {
        EntityRenderer<? super LivingEntity> render = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (render instanceof LivingEntityRenderer<?, ?> renderer && renderer.getModel() instanceof BipedEntityModel<?> entityModel) {
            ((BipedEntityModel<LivingEntity>) entityModel).copyBipedStateTo(model);
        }
    }

    public static class Model extends BipedEntityModel<LivingEntity> {
        public Model(ModelPart root) {
            super(root);
            this.setVisible(false);
            this.body.visible = true;
        }

        public static TexturedModelData createTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData root = modelData.getRoot();
            ModelPartData body = root.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.NONE);
            body.addChild("collar", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -24.0F, -2.0F, 6.0F, 3.0F, 4.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            body.addChild("bell", ModelPartBuilder.create().uv(0, 7).cuboid(-0.5F, -23.0F, -2.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

            root.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.NONE);
            root.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create(), ModelTransform.NONE);
            root.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
            root.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
            root.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);
            root.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);

            return TexturedModelData.of(modelData, 64, 64);
        }
    }
}

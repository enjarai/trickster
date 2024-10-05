package dev.enjarai.trickster.mixin.client.polymorph;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.pond.PlayerRendererDuck;
import dev.enjarai.trickster.pond.QuadrupedDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements PlayerRendererDuck {
    @Shadow protected abstract void setModelPose(AbstractClientPlayerEntity player);

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    public void trickster$setModelPose(AbstractClientPlayerEntity player) {
        setModelPose(player);
    }

    @SuppressWarnings("unchecked")
    @Inject(
            method = "renderArm",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyRenderedArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                   AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        var disguise = ModEntityComponents.DISGUISE.get(player).getEntity();
        if (disguise != null) {
            var left = arm == getModel().leftArm;

            // This should be completely safe... in theory
            //noinspection rawtypes
            if (MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(disguise) instanceof LivingEntityRenderer livingEntityRenderer) {
                EntityModel<Entity> model = livingEntityRenderer.getModel();
                ModelPart newArm = null;

                if (model instanceof BipedEntityModel<?> bipedModel) {
                    newArm = left ? bipedModel.leftArm : bipedModel.rightArm;
                } else if (model instanceof QuadrupedEntityModel<?> quadrupedModel) {
                    var casty = (QuadrupedDuck) quadrupedModel;
                    newArm = left ? casty.trickster$getLeftFrontLeg() : casty.trickster$getRightFrontLeg();
                }
                // TODO potentially support more model types?

                if (newArm != null) {
                    model.handSwingProgress = 0.0F;
                    model.setAngles(disguise, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                    newArm.pitch = 0.0F;
                    newArm.render(matrices,
                            vertexConsumers.getBuffer(RenderLayer.getEntityCutout(livingEntityRenderer.getTexture(disguise))),
                            light, OverlayTexture.DEFAULT_UV);
                }
            }

            ci.cancel();
        }
    }
}

package dev.enjarai.trickster.mixin.client.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Unique
    protected float tickDelta;

    @Inject(
            method = "animateModel(Lnet/minecraft/entity/LivingEntity;FFF)V",
            at = @At("HEAD")
    )
    private void grabTickDelta(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        tickDelta = h;
    }

    @Inject(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;copyTransform(Lnet/minecraft/client/model/ModelPart;)V"
            )
    )
    protected void positionModelParts(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {

    }
}

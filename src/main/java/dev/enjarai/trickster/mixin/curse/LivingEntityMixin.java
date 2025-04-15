package dev.enjarai.trickster.mixin.curse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "getDimensions", at = @At("HEAD"), cancellable = true
    )
    private void morphDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        var component = ModEntityComponents.CURSE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                cir.setReturnValue(disguise.getDimensions(pose));
            }
        }
    }

    @WrapOperation(
            method = { "playHurtSound", "onDamaged" }, at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;"
            )
    )
    private SoundEvent morphHurtSound(LivingEntity instance, DamageSource source, Operation<SoundEvent> original) {
        var component = ModEntityComponents.CURSE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                return SoundEvents.ENTITY_CAT_HURT;
            }
        }
        return original.call(instance, source);
    }

    @ModifyExpressionValue(
            method = { "computeFallDamage", "canBreatheInWater" }, at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getType()Lnet/minecraft/entity/EntityType;"
            )
    )
    private EntityType<?> modifyEntityType(EntityType<?> original) {
        var component = ModEntityComponents.CURSE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                return disguise.getType();
            }
        }
        return original;
    }
}

package dev.enjarai.trickster.mixin.polymorph;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.pond.EntityDisguiseDuck;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EntityDisguiseDuck {
    @Mutable
    @Shadow @Final public LimbAnimator limbAnimator;

    @Shadow protected abstract @Nullable SoundEvent getHurtSound(DamageSource source);

    @Override
    public void trickster$setLimbAnimator(LimbAnimator animator) {
        limbAnimator = animator;
    }

    @Override
    public SoundEvent trickster$getHurtSound(DamageSource source) {
        return getHurtSound(source);
    }

    @Inject(
            method = "getDimensions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void morphDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        var component = ModEntityComponents.DISGUISE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                cir.setReturnValue(disguise.getDimensions(pose));
            }
        }
    }

    @Inject(
            method = "getHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void morphHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        var component = ModEntityComponents.DISGUISE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise instanceof EntityDisguiseDuck living) {
                cir.setReturnValue(living.trickster$getHurtSound(source));
            }
        }
    }
}

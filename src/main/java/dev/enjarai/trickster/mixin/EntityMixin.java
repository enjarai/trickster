package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "getFinalGravity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void applyGravityGrace(CallbackInfoReturnable<Double> cir) {
        if ((Object) this instanceof LivingEntity && ModEntityComponents.GRACE.get(this).isInGrace("gravity")) {
            cir.setReturnValue(0.0);
        }
    }

    @Inject(
            method = "playStepSounds(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At("TAIL")
    )
    private void playCollarJingle(BlockPos pos, BlockState state, CallbackInfo ci) {

    }
}

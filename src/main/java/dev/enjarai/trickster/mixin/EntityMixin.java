package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.CollarItem;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract World getWorld();

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
            method = "playStepSound",
            at = @At("TAIL")
    )
    private void playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LivingEntity) {
            CollarItem.playJingleQuestionMark((LivingEntity) (Object) this, false);
        }
    }

    @Inject(method = "setSneaking", at = @At("HEAD"))
    private void setSneaking(boolean sneaking, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LivingEntity) {
            CollarItem.playJingleQuestionMark((LivingEntity) (Object) this, true);
        }
    }
}

package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        if ((Object) this instanceof LivingEntity && ModEntityCumponents.GRACE.get(this).isInGrace("gravity")) {
            cir.setReturnValue(0.0);
        }
    }
}

package dev.enjarai.trickster.mixin.polymorph;

import dev.enjarai.trickster.cca.ModEntityComponents;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(
            method = "playAmbientSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelAmbientSound(CallbackInfo ci) {
        var component = ModEntityComponents.DISGUISE.getNullable(this);
        if (component != null) {
            var disguise = component.getEntity();
            if (disguise != null) {
                ci.cancel();
            }
        }
    }
}

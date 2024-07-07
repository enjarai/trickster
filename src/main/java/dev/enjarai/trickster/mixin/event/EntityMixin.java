package dev.enjarai.trickster.mixin.event;

import dev.enjarai.trickster.spell.ItemTriggerProvider;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements ItemTriggerProvider {
    @Inject(method = "onLanding", at = @At("HEAD"))
    private void triggerItemSpell(CallbackInfo ci) {
        var self = (Entity)(Object)this;

        if (self.fallDistance <= 2.5)
            return;

        if (self instanceof ServerPlayerEntity player) {
            trickster$triggerBoots(player, new NumberFragment(self.fallDistance));
        }
    }
}

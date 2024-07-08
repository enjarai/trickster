package dev.enjarai.trickster.mixin.event;

import dev.enjarai.trickster.spell.ItemTriggerProvider;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemTriggerProvider {
    @Inject(method = "postHit", at = @At("RETURN"))
    private void triggerItemSpell(LivingEntity target, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            trickster$triggerMainHand(serverPlayer, EntityFragment.from(target));
        }
    }
}

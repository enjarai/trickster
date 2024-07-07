package dev.enjarai.trickster.mixin.event;

import dev.enjarai.trickster.spell.ItemTriggerProvider;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwordItem.class)
public class SwordItemMixin implements ItemTriggerProvider {
    @Inject(method = "postDamageEntity", at = @At("TAIL"))
    private void triggerItemSpell(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof ServerPlayerEntity player) {
            trickster$triggerMainHand(player, EntityFragment.from(target));
        }
    }
}

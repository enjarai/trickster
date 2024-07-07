package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.PlayerSpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SwordItem.class)
public class SwordItemMixin {
    @Inject(method = "postDamageEntity", at = @At("TAIL"))
    private void triggerItemSpell(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof ServerPlayerEntity player) {
            var spellComponent = stack.get(ModComponents.SPELL);

            if (spellComponent != null) {
                var ctx = new PlayerSpellContext(player, EquipmentSlot.MAINHAND);
                ctx.pushPartGlyph(List.of(EntityFragment.from(target)));
                spellComponent.spell().run(ctx);
                ctx.popPartGlyph();
            }
        }
    }
}

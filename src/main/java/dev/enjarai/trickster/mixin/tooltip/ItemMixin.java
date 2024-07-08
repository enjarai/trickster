package dev.enjarai.trickster.mixin.tooltip;


import dev.enjarai.trickster.SpellTooltipData;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
        private void trickster$getSpellTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        var spellComponent = stack.get(ModComponents.SPELL);
        if (spellComponent != null && !spellComponent.spell().isEmpty()) {
            cir.setReturnValue(Optional.of(new SpellTooltipData(spellComponent.spell())));
        }
    }
}

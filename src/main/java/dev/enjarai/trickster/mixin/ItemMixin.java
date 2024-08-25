package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.SpellTooltipData;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyReturnValue(
            method = "hasGlint",
            at = @At("RETURN")
    )
    private boolean spellGlint(boolean original, ItemStack stack) {
        return original
                || (stack.get(ModComponents.SPELL) instanceof SpellComponent spellComponent)
                && !spellComponent.spell().isEmpty()
                && !stack.isIn(ModItems.NO_SPELL_GLINT);
    }

    @Inject(
            method = "appendTooltip",
            at = @At("HEAD")
    )
    private void addGarble(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        var spellComponent = stack.get(ModComponents.SPELL);

        if (spellComponent != null && /*!spellComponent.spell().isEmpty() &&*/ spellComponent.closed()) {
            tooltip.add(spellComponent.name()
                    .flatMap(str -> Optional.of(Text.literal(str)))
                    .orElse(Text.literal("Mortal eyes upon my carvings").setStyle(Style.EMPTY.withObfuscated(true))));
        }
    }

    @Inject(
            method = "getTooltipData",
            at = @At("HEAD"),
            cancellable = true
    )
    private void trickster$getSpellTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        var spellComponent = stack.get(ModComponents.SPELL);

        if (spellComponent != null && !spellComponent.spell().isEmpty() && !spellComponent.closed()) {
            cir.setReturnValue(Optional.of(new SpellTooltipData(spellComponent.spell())));
        }
    }
}

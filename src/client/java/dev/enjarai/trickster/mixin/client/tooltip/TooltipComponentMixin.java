package dev.enjarai.trickster.mixin.client.tooltip;

import dev.enjarai.trickster.SpellTooltipData;
import dev.enjarai.trickster.render.SpellTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void trickster$getTooltipData(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof SpellTooltipData spellData)
            cir.setReturnValue(new SpellTooltipComponent(spellData.contents()));
    }
}

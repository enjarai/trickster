package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("RETURN"))
    private void ensureValidity(CallbackInfo ci) {
        var left = this.input.getStack(0);
        var middle = this.input.getStack(1);
        var spellComponent = middle.get(ModComponents.SPELL);

        if (!left.isEmpty() && middle.isOf(Items.ENCHANTED_BOOK) && spellComponent != null && this.levelCost.get() <= 0) {
            var newStack = left.copy();
            newStack.set(ModComponents.SPELL, spellComponent);
            this.output.setStack(0, newStack);
            this.levelCost.set(1);
        }
    }

    @ModifyArg(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"),
            index = 1
    )
    private ItemStack applySpell(ItemStack stack) {
        var spellComponent = this.input.getStack(1).get(ModComponents.SPELL);

        if (spellComponent != null) {
            stack.set(ModComponents.SPELL, spellComponent);
        }

        return stack;
    }
}

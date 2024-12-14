package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
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
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent;getEnchantmentEntries()Ljava/util/Set;"))
    private void enableSpellTransfer(CallbackInfo ci, @Local(ordinal = 0) LocalIntRef i, @Local(ordinal = 1) LocalBooleanRef bl2) {
        var left = this.input.getStack(0);
        var middle = this.input.getStack(1);

        if (middle.contains(ModComponents.FRAGMENT) && left.getCount() <= 1 && !left.isOf(Items.BOOK)) {
            i.set(i.get() + 1);
            bl2.set(true);
        }
    }

    @ModifyArg(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"), index = 1)
    private ItemStack applySpell(ItemStack stack) {
        var spellComponent = this.input.getStack(1).get(ModComponents.FRAGMENT);

        if (spellComponent != null) {
            stack.set(ModComponents.FRAGMENT, spellComponent);
        }

        return stack;
    }
}

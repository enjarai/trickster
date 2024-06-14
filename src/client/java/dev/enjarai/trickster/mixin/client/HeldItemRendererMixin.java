package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @WrapOperation(
            method = "updateHeldItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean cancelItemSwapAnimation(ItemStack left, ItemStack right, Operation<Boolean> original) {
        var originalValue = original.call(left, right);

        if (left.isIn(ModItems.HOLDABLE_HAT) && right.isIn(ModItems.HOLDABLE_HAT)) {
            return true;
        }

        return originalValue;
    }
}

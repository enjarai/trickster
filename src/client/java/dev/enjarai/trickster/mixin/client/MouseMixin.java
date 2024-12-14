package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.trickster.ModKeyBindings;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @WrapWithCondition(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
    private boolean interceptMouseScroll(PlayerInventory instance, double scrollAmount) {
        return !ModKeyBindings.interceptScroll((float) scrollAmount);
    }
}


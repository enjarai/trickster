package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.trickster.ModKeyBindings;
import dev.enjarai.trickster.screen.SpellCircleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
	@WrapWithCondition(
			method = "onMouseScroll",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"
			)
	)
	private boolean interceptMouseScroll(PlayerInventory instance, double scrollAmount) {
		return !ModKeyBindings.interceptScroll((float) scrollAmount);
	}
}
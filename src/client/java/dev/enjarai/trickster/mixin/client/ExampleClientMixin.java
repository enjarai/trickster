package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.screen.SpellCircleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class ExampleClientMixin extends Screen {
	protected ExampleClientMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("HEAD"), method = "init")
	private void init(CallbackInfo info) {
		addDrawableChild(ButtonWidget.builder(Text.of("TEST"), btn -> {
			MinecraftClient.getInstance().setScreen(new SpellCircleScreen());
		}).position(100, 100).size(100, 20).build());
	}
}
package dev.enjarai.trickster;

import dev.enjarai.trickster.screen.ModHandledScreens;
import dev.enjarai.trickster.screen.owo.GlyphComponent;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;

public class TricksterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModHandledScreens.register();
		ModKeyBindings.register();

		UIParsing.registerFactory(Trickster.id("glyph"), GlyphComponent::parse);
	}
}
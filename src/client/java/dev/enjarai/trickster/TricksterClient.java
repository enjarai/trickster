package dev.enjarai.trickster;

import dev.enjarai.trickster.screen.ModHandledScreens;
import net.fabricmc.api.ClientModInitializer;

public class TricksterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModHandledScreens.register();
	}
}
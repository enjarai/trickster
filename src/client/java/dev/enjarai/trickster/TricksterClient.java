package dev.enjarai.trickster;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.net.IsEditingScrollPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.particle.ProtectedBlockParticle;
import dev.enjarai.trickster.render.ShadowBlockEntityRenderer;
import dev.enjarai.trickster.render.SpellCircleBlockEntityRenderer;
import dev.enjarai.trickster.screen.ModHandledScreens;
import dev.enjarai.trickster.screen.ScrollAndQuillScreen;
import dev.enjarai.trickster.screen.owo.GlyphComponent;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TricksterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModHandledScreens.register();
		ModKeyBindings.register();

		BlockEntityRendererFactories.register(ModBlocks.SPELL_CIRCLE_ENTITY, SpellCircleBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(ModBlocks.SHADOW_ENTITY, ShadowBlockEntityRenderer::new);

		UIParsing.registerFactory(Trickster.id("glyph"), GlyphComponent::parseTrick);
		UIParsing.registerFactory(Trickster.id("pattern"), GlyphComponent::parseList);

		ParticleFactoryRegistry.getInstance().register(ModParticles.PROTECTED_BLOCK, ProtectedBlockParticle.Factory::new);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				var is_editing = client.currentScreen instanceof ScrollAndQuillScreen;
				ModNetworking.CHANNEL.clientHandle().send(new IsEditingScrollPacket(is_editing));
			}
		});
	}
}
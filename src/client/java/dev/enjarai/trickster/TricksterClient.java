package dev.enjarai.trickster;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.net.IsEditingScrollPacket;
import dev.enjarai.trickster.net.ModClientNetworking;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.particle.ProtectedBlockParticle;
import dev.enjarai.trickster.particle.SpellParticle;
import dev.enjarai.trickster.render.BarsRenderer;
import dev.enjarai.trickster.render.CircleErrorRenderer;
import dev.enjarai.trickster.render.HoldableHatRenderer;
import dev.enjarai.trickster.render.SpellCircleBlockEntityRenderer;
import dev.enjarai.trickster.screen.ModHandledScreens;
import dev.enjarai.trickster.screen.ScrollAndQuillScreen;
import dev.enjarai.trickster.screen.SignScrollScreen;
import dev.enjarai.trickster.screen.owo.GlyphComponent;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TricksterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScrollAndQuillItem.screenOpener = (text, hand) -> {
			MinecraftClient.getInstance().setScreen(new SignScrollScreen(text, hand));
		};

		ModHandledScreens.register();
		ModKeyBindings.register();
		ModClientNetworking.register();

		BlockEntityRendererFactories.register(ModBlocks.SPELL_CIRCLE_ENTITY, SpellCircleBlockEntityRenderer::new);

		UIParsing.registerFactory(Trickster.id("glyph"), GlyphComponent::parseTrick);
		UIParsing.registerFactory(Trickster.id("pattern"), GlyphComponent::parseList);

		ParticleFactoryRegistry.getInstance().register(ModParticles.PROTECTED_BLOCK, ProtectedBlockParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.SPELL, SpellParticle.Factory::new);

		AccessoriesRendererRegistry.registerRenderer(ModItems.TOP_HAT, HoldableHatRenderer::new);

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPELL_RESONATOR, RenderLayer.getCutout());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				var editing = client.currentScreen instanceof ScrollAndQuillScreen;
				var serverEditing = ModEntityCumponents.IS_EDITING_SCROLL.get(client.player).isEditing();
				if (editing != serverEditing) {
					ModNetworking.CHANNEL.clientHandle().send(new IsEditingScrollPacket(editing));
				}
			}
		});

		HudRenderCallback.EVENT.register(BarsRenderer::render);
		HudRenderCallback.EVENT.register(CircleErrorRenderer::render);
	}
}
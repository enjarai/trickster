package dev.enjarai.trickster;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.compat.ModCompat;
import dev.enjarai.trickster.compat.transmog.TransmogCompat;
import dev.enjarai.trickster.config.TricksterConfig;
import dev.enjarai.trickster.effects.ModEffects;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.recipe.ModRecipes;
import dev.enjarai.trickster.misc.ModDamageTypes;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.screen.ModScreenHandlers;
import dev.enjarai.trickster.spell.trick.Tricks;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;
import nl.enjarai.cicada.api.util.ProperLogger;
import org.slf4j.Logger;

public class Trickster implements ModInitializer, CicadaEntrypoint {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "trickster";
    public static final Logger LOGGER = ProperLogger.getLogger(MOD_ID);

	public static final Identifier SPELL_CIRCLE_ATTRIBUTE = id("spell_circle");
	public static final EntityAttributeModifier NEGATE_ATTRIBUTE = new EntityAttributeModifier(Trickster.SPELL_CIRCLE_ATTRIBUTE, -1d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	public static final TricksterConfig CONFIG = TricksterConfig.createAndLoad();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModBlocks.register();
		ModComponents.register();
		ModItems.register();
		ModEffects.register();
		ModScreenHandlers.register();
		ModNetworking.register();
		ModParticles.register();
		ModSounds.register();
		ModAttachments.register();
		ModRecipes.register();
		ModDamageTypes.register();
		Tricks.register();
		ModCriteria.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			TricksterCommand.register(dispatcher);
		});

		if (ModCompat.TRANSMOG_LOADED) {
			TransmogCompat.init();
		}
	}

	@Override
	public void registerConversations(ConversationManager conversationManager) {
		conversationManager.registerSource(
				JsonSource.fromUrl("https://raw.githubusercontent.com/enjarai/trickster/master/src/main/resources/cicada/trickster/conversations.json")
						.or(JsonSource.fromResource("cicada/trickster/conversations.json")),
				LOGGER::info
		);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
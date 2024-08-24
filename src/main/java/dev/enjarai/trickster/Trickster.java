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
import dev.enjarai.trickster.spell.ItemTriggerHelper;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Tricks;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.util.ProperLogger;
import org.slf4j.Logger;

public class Trickster implements ModInitializer {
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

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayerEntity serverPlayer)
				ItemTriggerHelper.triggerMainHand(serverPlayer, VectorFragment.of(pos));
			else return true;

			var newState = world.getBlockState(pos);
            return newState.getBlock() == state.getBlock() || newState.getHardness(world, pos) > 0;
        });

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			TricksterCommand.register(dispatcher);
		});

		if (ModCompat.TRANSMOG_LOADED) {
			TransmogCompat.init();
		}
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
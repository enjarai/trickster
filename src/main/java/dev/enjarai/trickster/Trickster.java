package dev.enjarai.trickster;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.config.TricksterConfig;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.recipe.ModRecipes;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.screen.ModScreenHandlers;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trickster implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "trickster";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		ModScreenHandlers.register();
		ModNetworking.register();
		ModParticles.register();
		ModSounds.register();
		ModAttachments.register();
		ModRecipes.register();
		Tricks.register();
		SpellCircleEvent.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
package dev.enjarai.trickster.cca;

import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;

import dev.enjarai.trickster.block.ModularSpellConstructBlockEntity;
import dev.enjarai.trickster.block.SpellConstructBlockEntity;

public class ModBlockComponents implements BlockComponentInitializer {
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(ModularSpellConstructBlockEntity.class, ModUwuComponents.WARD_BYPASS, be -> new WardBypassComponent());
        registry.registerFor(SpellConstructBlockEntity.class, ModUwuComponents.WARD_BYPASS, be -> new WardBypassComponent());
    }
}

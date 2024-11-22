package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.datagen.provider.BlockConversionProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CoolBlockConversionGenerator extends BlockConversionProvider {
    public CoolBlockConversionGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, "cool", registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        registerCoolConversion(Blocks.WATER, Blocks.ICE);
        registerCoolConversion(Blocks.ICE, Blocks.PACKED_ICE);
        registerCoolConversion(Blocks.PACKED_ICE, Blocks.BLUE_ICE);
        registerCoolConversion(Blocks.LAVA, Blocks.OBSIDIAN);
        registerCoolConversion(Blocks.MAGMA_BLOCK, Blocks.NETHERRACK);
        registerCoolConversion(Blocks.OAK_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.SPRUCE_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.BIRCH_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.JUNGLE_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.ACACIA_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.DARK_OAK_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.CHERRY_SAPLING, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.AZALEA, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.FLOWERING_AZALEA, Blocks.DEAD_BUSH);
        registerCoolConversion(Blocks.CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
    }

    public void registerCoolConversion(Block block, Block... conversions) {
        Builder builder = getOrCreateConversion(block);
        for (Block conversion : conversions) {
            builder.add(conversion, 1);
        }
    }
}

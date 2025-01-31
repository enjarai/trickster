package dev.enjarai.trickster.datagen;

import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.datagen.provider.BlockConversionProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;

public class ErodeBlockConversionGenerator extends BlockConversionProvider {
    public ErodeBlockConversionGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, "erode", registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        registerErosionConversion(Blocks.STONE, Blocks.COBBLESTONE);
        registerErosionConversion(Blocks.COBBLESTONE, Blocks.GRAVEL, Blocks.MOSSY_COBBLESTONE);
        registerErosionConversion(Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS);
        registerErosionConversion(Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB);
        registerErosionConversion(Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL);
        registerErosionConversion(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        registerErosionConversion(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        registerErosionConversion(Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        registerErosionConversion(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL);
        registerErosionConversion(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        registerErosionConversion(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS);
        registerErosionConversion(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        registerErosionConversion(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS);
        registerErosionConversion(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        registerErosionConversion(Blocks.SANDSTONE, Blocks.SAND);
        registerErosionConversion(Blocks.GILDED_BLACKSTONE, Blocks.BLACKSTONE);
        registerErosionConversion(Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS);
        registerErosionConversion(Blocks.ANVIL, Blocks.CHIPPED_ANVIL);
        registerErosionConversion(Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL);
        registerErosionConversion(Blocks.DAMAGED_ANVIL, Blocks.AIR);
        registerErosionConversion(Blocks.GRAVEL, Blocks.SAND);
        //region Copper
        registerErosionConversion(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER);
        registerErosionConversion(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER);
        registerErosionConversion(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER);
        registerErosionConversion(Blocks.CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER);
        registerErosionConversion(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER);
        registerErosionConversion(Blocks.WEATHERED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER);
        registerErosionConversion(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE);
        registerErosionConversion(Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE);
        registerErosionConversion(Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE);
        registerErosionConversion(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
        registerErosionConversion(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);
        registerErosionConversion(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
        registerErosionConversion(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS);
        registerErosionConversion(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS);
        registerErosionConversion(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
        registerErosionConversion(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB);
        registerErosionConversion(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB);
        registerErosionConversion(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
        registerErosionConversion(Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR);
        registerErosionConversion(Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR);
        registerErosionConversion(Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR);
        registerErosionConversion(Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR);
        registerErosionConversion(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR);
        registerErosionConversion(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR);
        registerErosionConversion(Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB);
        registerErosionConversion(Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB);
        registerErosionConversion(Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB);
        //endregion
    }

    public void registerErosionConversion(Block block, Block... conversions) {
        Builder builder = getOrCreateConversion(block);
        for (Block conversion : conversions) {
            builder.add(conversion, 1);
        }
    }
}

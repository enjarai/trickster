package dev.enjarai.trickster.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        registerHeatConversion(Blocks.STONE, Blocks.DEEPSLATE);
        registerHeatConversion(Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
        registerHeatConversion(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
        registerHeatConversion(Blocks.DEEPSLATE, Blocks.MAGMA_BLOCK);
        registerHeatConversion(Blocks.COBBLED_DEEPSLATE, Blocks.MAGMA_BLOCK);
        registerHeatConversion(Blocks.DIORITE, Blocks.TUFF);
        registerHeatConversion(Blocks.ANDESITE, Blocks.TUFF);
        registerHeatConversion(Blocks.GRANITE, Blocks.TUFF);
        registerHeatConversion(Blocks.MAGMA_BLOCK, Blocks.LAVA);
        registerHeatConversion(Blocks.MUD, Blocks.PACKED_MUD);
        registerHeatConversion(Blocks.SAND, Blocks.GLASS);
        registerHeatConversion(Blocks.RED_SAND, Blocks.ORANGE_STAINED_GLASS);
        registerHeatConversion(Blocks.RAW_IRON_BLOCK, Blocks.IRON_BLOCK);
        registerHeatConversion(Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_BLOCK);
        registerHeatConversion(Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_BLOCK);
        registerHeatConversion(Blocks.BLUE_ICE, Blocks.PACKED_ICE);
        registerHeatConversion(Blocks.PACKED_ICE, Blocks.ICE);
        registerHeatConversion(Blocks.ICE, Blocks.WATER);
        registerHeatConversion(Blocks.WATER_CAULDRON, Blocks.CAULDRON);
        registerHeatConversion(Blocks.DIRT, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.DIRT_PATH, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.PODZOL, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.MYCELIUM, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.ROOTED_DIRT, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.FARMLAND, Blocks.COARSE_DIRT);
        registerHeatConversion(Blocks.WATER, Blocks.AIR);
        registerHeatConversion(Blocks.SNOW, Blocks.AIR);
        registerHeatConversion(Blocks.SNOW_BLOCK, Blocks.AIR);
        registerHeatConversion(Blocks.OAK_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.SPRUCE_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.BIRCH_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.JUNGLE_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.ACACIA_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.DARK_OAK_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.CHERRY_SAPLING, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.AZALEA, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.FLOWERING_AZALEA, Blocks.DEAD_BUSH);
        registerHeatConversion(Blocks.WET_SPONGE, Blocks.SPONGE);
        registerHeatConversion(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        registerHeatConversion(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        registerHeatConversion(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        registerHeatConversion(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
        registerHeatConversion(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        registerHeatConversion(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
        registerHeatConversion(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE);
        registerHeatConversion(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        registerHeatConversion(Blocks.COBWEB, Blocks.AIR);
        registerHeatConversion(Blocks.COAL_BLOCK, Blocks.FIRE);
        registerHeatConversion(Blocks.INFESTED_STONE, Blocks.STONE);
        registerHeatConversion(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        registerHeatConversion(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        registerHeatConversion(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        registerHeatConversion(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        registerHeatConversion(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        registerHeatConversion(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);

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
        registerCoolConversion(Blocks.INFESTED_STONE, Blocks.STONE);
        registerCoolConversion(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        registerCoolConversion(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        registerCoolConversion(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        registerCoolConversion(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        registerCoolConversion(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        registerCoolConversion(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);

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

    public void registerHeatConversion(Block block, Block... conversions) {
        var tag = TagKey.of(RegistryKeys.BLOCK, Registries.BLOCK.getId(block).withPrefixedPath("trickster/conversion/heat/"));
        getOrCreateTagBuilder(tag).add(conversions);
    }

    public void registerCoolConversion(Block block, Block... conversions) {
        var tag = TagKey.of(RegistryKeys.BLOCK, Registries.BLOCK.getId(block).withPrefixedPath("trickster/conversion/cool/"));
        getOrCreateTagBuilder(tag).add(conversions);
    }

    public void registerErosionConversion(Block block, Block... conversions) {
        var tag = TagKey.of(RegistryKeys.BLOCK, Registries.BLOCK.getId(block).withPrefixedPath("trickster/conversion/erosion/"));
        getOrCreateTagBuilder(tag).add(conversions);
    }
}

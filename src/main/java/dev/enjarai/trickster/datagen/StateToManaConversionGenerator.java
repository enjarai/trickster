package dev.enjarai.trickster.datagen;

import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.datagen.provider.StateToManaConversionProvider;
import dev.enjarai.trickster.mixin.accessor.CropBlockAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property.Value;

public class StateToManaConversionGenerator extends StateToManaConversionProvider {
    public StateToManaConversionGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        //region Tags
        getOrCreateConversion(BlockTags.LEAVES, Blocks.ACACIA_LEAVES).add(7.12f); // this is for the sake of Evelyn's suffering
        getOrCreateConversion(BlockTags.CAVE_VINES, Blocks.CAVE_VINES_PLANT).add(16, new Value<>(Properties.BERRIES, true)).add(4);
        getOrCreateConversion(BlockTags.SMALL_FLOWERS, Blocks.POPPY).add(4);
        getOrCreateConversion(BlockTags.TALL_FLOWERS, Blocks.ROSE_BUSH).add(8);

        //region Misc
        getOrCreateConversion(Blocks.MELON).add(24);
        getOrCreateConversion(Blocks.MELON_STEM).add(4);
        getOrCreateConversion(Blocks.PUMPKIN).add(24);
        getOrCreateConversion(Blocks.CARVED_PUMPKIN).add(16);
        getOrCreateConversion(Blocks.PUMPKIN_STEM).add(4);
        getOrCreateConversion(Blocks.BAMBOO).add(2);
        getOrCreateConversion(Blocks.BAMBOO_SAPLING).add(0);
        getOrCreateConversion(Blocks.SUGAR_CANE).add(12);
        getOrCreateConversion(Blocks.CACTUS).add(24);
        getOrCreateConversion(Blocks.KELP).add(2);
        getOrCreateConversion(Blocks.KELP_PLANT).add(2);
        getOrCreateConversion(Blocks.BROWN_MUSHROOM).add(8);
        getOrCreateConversion(Blocks.RED_MUSHROOM).add(8);
        getOrCreateConversion(Blocks.BROWN_MUSHROOM_BLOCK).add(24);
        getOrCreateConversion(Blocks.RED_MUSHROOM_BLOCK).add(24);
        getOrCreateConversion(Blocks.MUSHROOM_STEM).add(24);
        getOrCreateConversion(Blocks.MOSS_BLOCK).add(8);
        getOrCreateConversion(Blocks.MOSS_CARPET).add(1);
        getOrCreateConversion(Blocks.AZALEA).add(6);
        getOrCreateConversion(Blocks.FLOWERING_AZALEA).add(6.1f); // flowers are tasty :3
        getOrCreateConversion(Blocks.SHORT_GRASS).add(2);
        getOrCreateConversion(Blocks.TALL_GRASS).add(2);
        getOrCreateConversion(Blocks.SEAGRASS).add(2);
        getOrCreateConversion(Blocks.TALL_SEAGRASS).add(2);
        getOrCreateConversion(Blocks.SWEET_BERRY_BUSH)
                .add(0, new Value<>(Properties.AGE_3, 0))
                .add(4, new Value<>(Properties.AGE_3, 1))
                .add(12, new Value<>(Properties.AGE_3, 2))
                .add(16, new Value<>(Properties.AGE_3, 3));

        //region Clustered
        configureClustered(Blocks.PINK_PETALS, FlowerbedBlock.FLOWER_AMOUNT, 4, 1);
        configureClustered(Blocks.SEA_PICKLE, SeaPickleBlock.PICKLES, 4, 1);

        //region Crops
        configureCrop(Blocks.CARROTS);
        configureCrop(Blocks.POTATOES);
        configureCrop(Blocks.WHEAT);
        configureCrop(Blocks.BEETROOTS);
    }

    private void configureClustered(Block block, IntProperty property, int max, float value) {
        var builder = getOrCreateConversion(block);

        for (int i = 1; i <= max; i++) {
            builder.add(i * value, new Value<>(property, i));
        }
    }

    private void configureCrop(Block block) {
        if (block instanceof CropBlock cropBlock) {
            var builder = getOrCreateConversion(block);
            var property = ((CropBlockAccessor) cropBlock).callGetAgeProperty();
            int maxAge = cropBlock.getMaxAge();

            for (int i = 0; i < maxAge; i++) {
                builder.add(i * 2, new Value<>(property, i));
            }

            builder.add(32, new Value<>(property, maxAge));
        } else throw new IllegalStateException("Cannot generate crop mana values for a block which does not extend CropBlock");
    }
}

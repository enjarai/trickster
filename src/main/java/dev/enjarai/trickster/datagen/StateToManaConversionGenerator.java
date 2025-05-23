package dev.enjarai.trickster.datagen;

import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.datagen.provider.StateToManaConversionProvider;
import dev.enjarai.trickster.mixin.accessor.CropBlockAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property.Value;

public class StateToManaConversionGenerator extends StateToManaConversionProvider {
    public StateToManaConversionGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        //region Tags
        createConversion(BlockTags.LEAVES).add(7.12f); // this is for the sake of Evelyn's suffering
        createConversion(BlockTags.CAVE_VINES).add(16, new Value<>(Properties.BERRIES, true)).add(4);
        createConversion(BlockTags.SMALL_FLOWERS).add(4);
        createConversion(BlockTags.TALL_FLOWERS).add(8);

        //region Misc
        copyOrCreateConversion(Blocks.MELON).add(24);
        copyOrCreateConversion(Blocks.MELON_STEM).add(4);
        copyOrCreateConversion(Blocks.PUMPKIN).add(24);
        copyOrCreateConversion(Blocks.CARVED_PUMPKIN).add(16);
        copyOrCreateConversion(Blocks.PUMPKIN_STEM).add(4);
        copyOrCreateConversion(Blocks.BAMBOO).add(2);
        copyOrCreateConversion(Blocks.BAMBOO_SAPLING).add(0);
        copyOrCreateConversion(Blocks.SUGAR_CANE).add(12);
        copyOrCreateConversion(Blocks.CACTUS).add(24);
        copyOrCreateConversion(Blocks.KELP).add(2);
        copyOrCreateConversion(Blocks.KELP_PLANT).add(2);
        copyOrCreateConversion(Blocks.SWEET_BERRY_BUSH)
            .add(0, new Value<>(Properties.AGE_3, 0))
            .add(4, new Value<>(Properties.AGE_3, 1))
            .add(12, new Value<>(Properties.AGE_3, 2))
            .add(16, new Value<>(Properties.AGE_3, 3));
        copyOrCreateConversion(Blocks.BROWN_MUSHROOM).add(8);
        copyOrCreateConversion(Blocks.RED_MUSHROOM).add(8);
        copyOrCreateConversion(Blocks.BROWN_MUSHROOM_BLOCK).add(24);
        copyOrCreateConversion(Blocks.RED_MUSHROOM_BLOCK).add(24);
        copyOrCreateConversion(Blocks.MUSHROOM_STEM).add(24);
        copyOrCreateConversion(Blocks.MOSS_BLOCK).add(8);
        copyOrCreateConversion(Blocks.MOSS_CARPET).add(1);
        copyOrCreateConversion(Blocks.AZALEA).add(6);
        copyOrCreateConversion(Blocks.FLOWERING_AZALEA).add(6.1f); // flowers are tasty :3
        copyOrCreateConversion(Blocks.SHORT_GRASS).add(2);
        copyOrCreateConversion(Blocks.TALL_GRASS).add(2);
        copyOrCreateConversion(Blocks.SEAGRASS).add(2);
        copyOrCreateConversion(Blocks.TALL_SEAGRASS).add(2);

        //region Crops
        configureCrop(Blocks.CARROTS);
        configureCrop(Blocks.POTATOES);
        configureCrop(Blocks.WHEAT);
        configureCrop(Blocks.BEETROOTS);
    }

    private void configureCrop(Block block) {
        if (block instanceof CropBlock cropBlock) {
            var builder = copyOrCreateConversion(block);
            var property = ((CropBlockAccessor) cropBlock).callGetAgeProperty();
            int maxAge = cropBlock.getMaxAge();

            for (int i = 0; i < maxAge; i++) {
                builder.add(i * 2, new Value<>(property, i));
            }

            builder.add(32, new Value<>(property, maxAge));
        } else throw new IllegalStateException("Cannot generate crop mana values for a block which does not extend CropBlock");
    }
}

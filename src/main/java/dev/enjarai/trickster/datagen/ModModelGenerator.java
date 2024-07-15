package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellResonatorBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.SPELL_RESONATOR)
                .coordinate(BlockStateVariantMap.create(SpellResonatorBlock.FACING, SpellResonatorBlock.POWER)
                        .register((direction, i) ->
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, i != 0 ? Trickster.id("block/spell_resonator_enabled") : Trickster.id("block/spell_resonator"))
                                        .put(VariantSettings.Y, switch (direction) {
                                            case UP, DOWN, NORTH -> VariantSettings.Rotation.R0;
                                            case SOUTH -> VariantSettings.Rotation.R180;
                                            case EAST -> VariantSettings.Rotation.R90;
                                            case WEST -> VariantSettings.Rotation.R270;
                                        })
                                        .put(VariantSettings.X, switch (direction) {
                                            case UP -> VariantSettings.Rotation.R0;
                                            case DOWN -> VariantSettings.Rotation.R180;
                                            case NORTH, EAST, WEST, SOUTH -> VariantSettings.Rotation.R90;
                                        })
                        )
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}

package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellResonatorBlock;
import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;

import java.util.List;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.SPELL_RESONATOR)
                .coordinate(BlockStateVariantMap.create(SpellResonatorBlock.FACING, SpellResonatorBlock.POWER)
                        .register((direction, i) -> BlockStateVariant.create()
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
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(ModBlocks.SCROLL_SHELF, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        for (var block : List.of(ModBlocks.MODULAR_SPELL_CONSTRUCT, ModBlocks.SPELL_CONSTRUCT, ModBlocks.CHARGING_ARRAY)) {
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                    .coordinate(BlockStateVariantMap.create(Properties.FACING)
                            .register(direction -> BlockStateVariant.create()
                                    .put(VariantSettings.MODEL, Registries.BLOCK.getId(block).withPrefixedPath("block/"))
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
        blockStateModelGenerator.registerSimpleState(ModBlocks.LIGHT);
        blockStateModelGenerator.registerSingleton(ModBlocks.INERT_SPAWNER, TexturedModel.CUBE_ALL_INNER_FACES);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.WAND, Models.HANDHELD);

        itemModelGenerator.register(ModItems.AMETHYST_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_AMETHYST_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.QUARTZ_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_QUARTZ_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_EMERALD_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_DIAMOND_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ECHO_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_ECHO_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASTRAL_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_ASTRAL_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COMMAND_KNOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUSTED_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.OMINOUS_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.INACTIVE_SPAWNER_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPAWNER_SPELL_CORE, Models.GENERATED);

        itemModelGenerator.register(ModItems.SCROLL_AND_QUILL, Models.GENERATED);
        itemModelGenerator.register(ModItems.WRITTEN_SCROLL, Models.GENERATED);

        itemModelGenerator.register(ModItems.TOME_OF_TOMFOOLERY, Models.GENERATED);
        itemModelGenerator.register(ModItems.WARDING_CHARM, Models.GENERATED);
        itemModelGenerator.register(ModItems.AMETHYST_WHORL, Models.GENERATED);
        itemModelGenerator.register(ModItems.COLLAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.LEASH, Models.GENERATED);
        itemModelGenerator.register(ModItems.MACRO_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELL_INK, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELL_RESONATOR_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELL_RING, Models.GENERATED);

        // For the NaN advancement
        itemModelGenerator.register(ModItems.NAN, Models.GENERATED);

        ModItems.DYED_VARIANTS.forEach(v -> {
            itemModelGenerator.register(v.variant(), Models.GENERATED);
        });
    }
}

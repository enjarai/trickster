package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.cauldron.EraseSpellCauldronBehavior;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlocks {
    public static final SpellResonatorBlock SPELL_RESONATOR = register("spell_resonator", new SpellResonatorBlock());
    public static final SpellConstructBlock SPELL_CONSTRUCT = register("spell_construct", new SpellConstructBlock());
    public static final LightBlock LIGHT = register("light", new LightBlock());
    public static final ColorBlock COLOR_BLOCK = register("color_block", new ColorBlock());
    public static final ModularSpellConstructBlock MODULAR_SPELL_CONSTRUCT = register("modular_spell_construct", new ModularSpellConstructBlock());
    public static final ScrollShelfBlock SCROLL_SHELF = register("scroll_shelf", new ScrollShelfBlock());
    public static final ChargingArrayBlock CHARGING_ARRAY = register("charging_array", new ChargingArrayBlock());
    public static final Block INERT_SPAWNER = register("inert_spawner", new Block(AbstractBlock.Settings.copy(Blocks.SPAWNER)));

    public static final BlockEntityType<SpellConstructBlockEntity> SPELL_CONSTRUCT_ENTITY = BlockEntityType.Builder.create(SpellConstructBlockEntity::new, SPELL_CONSTRUCT).build(null);
    public static final BlockEntityType<LightBlockEntity> LIGHT_ENTITY = BlockEntityType.Builder.create(LightBlockEntity::new, LIGHT).build(null);
    public static final BlockEntityType<ColorBlockEntity> COLOR_BLOCK_ENTITY = BlockEntityType.Builder.create(ColorBlockEntity::new, COLOR_BLOCK).build(null);
    public static final BlockEntityType<ModularSpellConstructBlockEntity> MODULAR_SPELL_CONSTRUCT_ENTITY = BlockEntityType.Builder
            .create(ModularSpellConstructBlockEntity::new, MODULAR_SPELL_CONSTRUCT).build(null);
    public static final BlockEntityType<ScrollShelfBlockEntity> SCROLL_SHELF_ENTITY = BlockEntityType.Builder.create(ScrollShelfBlockEntity::new, SCROLL_SHELF).build(null);
    public static final BlockEntityType<ChargingArrayBlockEntity> CHARGING_ARRAY_ENTITY = BlockEntityType.Builder.create(ChargingArrayBlockEntity::new, CHARGING_ARRAY).build(null);

    public static final TagKey<Block> CANNOT_BREAK = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_break"));
    public static final TagKey<Block> CANNOT_SWAP = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_swap"));
    public static final TagKey<Block> CANNOT_LEVITATE = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_levitate"));
    public static final TagKey<Block> CONJURABLE_FLOWERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("conjurable_flowers"));

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Trickster.id(name), block);
    }

    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("spell_construct"), SPELL_CONSTRUCT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("light"), LIGHT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("color_block"), COLOR_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("modular_spell_construct"), MODULAR_SPELL_CONSTRUCT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("scroll_shelf"), SCROLL_SHELF_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("charging_array"), CHARGING_ARRAY_ENTITY);

        var cauldronMap = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map();
        cauldronMap.put(ModItems.WRITTEN_SCROLL, new EraseSpellCauldronBehavior());

        for (var scroll : ModItems.COLORED_WRITTEN_SCROLLS) {
            cauldronMap.put(scroll, new EraseSpellCauldronBehavior());
        }

        for (var scroll : ModItems.COLORED_SCROLLS_AND_QUILLS) {
            cauldronMap.put(scroll, new EraseSpellCauldronBehavior());
        }
    }
}

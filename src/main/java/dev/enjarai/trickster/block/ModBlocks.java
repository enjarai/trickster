package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.cauldron.EraseSpellCauldronBehavior;
import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
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
    public static final ModularSpellConstructBlock MODULAR_SPELL_CONSTRUCT = register("modular_spell_construct", new ModularSpellConstructBlock());
    public static final ScrollShelfBlock SCROLL_SHELF = register("scroll_shelf", new ScrollShelfBlock());

    public static final BlockEntityType<SpellConstructBlockEntity> SPELL_CONSTRUCT_ENTITY =
            FabricBlockEntityTypeBuilder.create(SpellConstructBlockEntity::new, SPELL_CONSTRUCT).build(null);
    public static final BlockEntityType<LightBlockEntity> LIGHT_ENTITY =
            FabricBlockEntityTypeBuilder.create(LightBlockEntity::new, LIGHT).build(null);
    public static final BlockEntityType<ModularSpellConstructBlockEntity> MODULAR_SPELL_CONSTRUCT_ENTITY =
            FabricBlockEntityTypeBuilder.create(ModularSpellConstructBlockEntity::new, MODULAR_SPELL_CONSTRUCT).build(null);
    public static final BlockEntityType<ScrollShelfBlockEntity> SCROLL_SHELF_ENTITY =
            FabricBlockEntityTypeBuilder.create(ScrollShelfBlockEntity::new, SCROLL_SHELF).build(null);

    public static final TagKey<Block> UNBREAKABLE = TagKey.of(RegistryKeys.BLOCK, Trickster.id("unbreakable"));
    public static final TagKey<Block> CONJURABLE_FLOWERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("conjurable_flowers"));

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Trickster.id(name), block);
    }

    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("spell_construct"), SPELL_CONSTRUCT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("light"), LIGHT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("modular_spell_construct"), MODULAR_SPELL_CONSTRUCT_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("scroll_shelf"), SCROLL_SHELF_ENTITY);

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

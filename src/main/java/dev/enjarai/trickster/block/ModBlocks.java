package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final SpellCircleBlock SPELL_CIRCLE = new SpellCircleBlock(AbstractBlock.Settings.create());

    public static final BlockEntityType<SpellCircleBlockEntity> SPELL_CIRCLE_ENTITY =
            BlockEntityType.Builder.create(SpellCircleBlockEntity::new, SPELL_CIRCLE).build(null);

    public static void register() {
        Registry.register(Registries.BLOCK, Trickster.id("spell_circle"), SPELL_CIRCLE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("spell_circle"), SPELL_CIRCLE_ENTITY);
    }
}

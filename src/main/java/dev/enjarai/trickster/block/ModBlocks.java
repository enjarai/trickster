package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class ModBlocks {
    public static final SpellCircleBlock SPELL_CIRCLE = new SpellCircleBlock(AbstractBlock.Settings.create()
            .strength(0.5f)
            .noCollision());

    public static final BlockEntityType<SpellCircleBlockEntity> SPELL_CIRCLE_ENTITY =
            BlockEntityType.Builder.create(SpellCircleBlockEntity::new, SPELL_CIRCLE).build(null);

    public static final PointOfInterestType SPELL_CIRCLE_POI =
            PointOfInterestHelper.register(Trickster.id("spell_circle"), 0, 2, SPELL_CIRCLE);

    public static void register() {
        Registry.register(Registries.BLOCK, Trickster.id("spell_circle"), SPELL_CIRCLE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id("spell_circle"), SPELL_CIRCLE_ENTITY);
    }
}

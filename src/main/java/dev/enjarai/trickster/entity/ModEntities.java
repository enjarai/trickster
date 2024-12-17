package dev.enjarai.trickster.entity;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModEntities {
    public static final TagKey<EntityType<?>> IRREPRESSIBLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Trickster.id("irrepressible"));

    public static final EntityType<AmethystProjectile> AMETHYST_SHARD = Registry.register(
            Registries.ENTITY_TYPE,
            Trickster.id("amethyst_shard"),
            EntityType.Builder.create(AmethystProjectile::new, SpawnGroup.MISC)
                    .dimensions(AmethystProjectile.dimensions, AmethystProjectile.dimensions)
                    .maxTrackingRange(4).trackingTickInterval(10) //wiki says its needed
                    .build()
    );

    public static void onInitialize() {
    }
}
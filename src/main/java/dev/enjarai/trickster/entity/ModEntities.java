package dev.enjarai.trickster.entity;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModEntities {
    public static final TagKey<EntityType<?>> IRREPRESSIBLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Trickster.id("irrepressible"));
}

package dev.enjarai.trickster.entity;

import dev.enjarai.trickster.Trickster;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModEntities {
    public static final EntityType<LevitatingBlockEntity> LEVITATING_BLOCK =
            Registry.register(Registries.ENTITY_TYPE, Trickster.id("levitating_block"),
                    EntityType.Builder.create(LevitatingBlockEntity::new, SpawnGroup.MISC).dimensions(1, 1).build());

    public static final TagKey<EntityType<?>> IRREPRESSIBLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Trickster.id("irrepressible"));

    public static void register() {

    }
}

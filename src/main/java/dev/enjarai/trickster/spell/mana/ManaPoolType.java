package dev.enjarai.trickster.spell.mana;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record ManaPoolType<T extends ManaPool>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaPoolType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_pool_type"));
    public static final Registry<ManaPoolType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final ManaPoolType<SimpleManaPool> SIMPLE = register("simple", SimpleManaPool.ENDEC);

    private static <T extends ManaPool> ManaPoolType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaPoolType<>(endec));
    }
}

package dev.enjarai.trickster.spell.mana;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record ManaPoolType<T extends ManaPool>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaPoolType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_pool_type"));
    public static final Registry<ManaPoolType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ManaPoolType<SimpleManaPool> SIMPLE = register("simple", SimpleManaPool.ENDEC);

    private static <T extends ManaPool> ManaPoolType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaPoolType<>(endec));
    }
}

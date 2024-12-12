package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record ManaPoolType<T extends ManaPool>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaPoolType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_pool_type"));
    public static final Registry<ManaPoolType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final ManaPoolType<SimpleManaPool> SIMPLE = register("simple", SimpleManaPool.ENDEC);
    public static final ManaPoolType<SharedManaPool> SHARED = register("shared", SharedManaPool.ENDEC);
    public static final ManaPoolType<InfiniteManaPool> INFINITE = register("infinite", InfiniteManaPool.ENDEC);

    private static <T extends MutableManaPool> ManaPoolType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaPoolType<>(endec));
    }

    public static void register() {
        // init the class :brombeere:
    }
}

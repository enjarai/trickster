package dev.enjarai.trickster.spell.mana.generation.event;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record ManaEventType<T extends ManaEvent>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaEventType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_event_type"));
    public static final Registry<ManaEventType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    private static <T extends ManaEvent> ManaEventType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaEventType<>(endec));
    }

    public static void register() {
        // init the class :brombeere:
    }
}

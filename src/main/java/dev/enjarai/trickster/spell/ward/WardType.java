package dev.enjarai.trickster.spell.ward;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record WardType<T extends Ward>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<WardType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("ward_type"));
    public static final Registry<WardType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final WardType<SimpleCubicWard> SIMPLE_CUBIC = register("simple_cubic", SimpleCubicWard.ENDEC);

    private static <T extends Ward> WardType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new WardType<>(endec));
    }

    public static void register() {
        // init the class :brombeere:
    }
}

package dev.enjarai.trickster.fleck;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record FleckType<T extends Fleck>(StructEndec<T> endec) {

    public static final RegistryKey<Registry<FleckType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fleck_type"));
    public static final Registry<FleckType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final FleckType<LineFleck> LINE = register("line", LineFleck.ENDEC);
    public static final FleckType<TextFleck> TEXT = register("text", TextFleck.ENDEC);
    public static final FleckType<SpellFleck> SPELL = register("spell", SpellFleck.ENDEC);

    private static <T extends Fleck> FleckType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new FleckType<>(endec));
    }
}

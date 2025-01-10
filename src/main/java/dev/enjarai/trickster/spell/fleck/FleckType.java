package dev.enjarai.trickster.spell.fleck;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record FleckType<T extends Fleck>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<FleckType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fleck_type"));
    public static final Registry<FleckType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final FleckType<LineFleck> LINE = register("line", LineFleck.ENDEC);
    public static final FleckType<TextFleck> TEXT = register("text", TextFleck.ENDEC);
    public static final FleckType<SpellFleck> SPELL = register("spell", SpellFleck.ENDEC);

    private static <T extends Fleck> FleckType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new FleckType<>(endec));
    }

    public static void register() {}
}
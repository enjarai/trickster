package dev.enjarai.trickster.spell.blot;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record BlotType<T extends Blot>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<BlotType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("blot_type"));
    public static final Registry<BlotType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final BlotType<LineBlot> LINE = register("line", LineBlot.ENDEC);
    public static final BlotType<SpellBlot> SPELL = register("spell", SpellBlot.ENDEC);

    private static <T extends Blot> BlotType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new BlotType<>(endec));
    }

    public static void register() {}
}

package dev.enjarai.trickster.spell.mana;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.handler.AmethystManaHandler;
import dev.enjarai.trickster.spell.mana.handler.DefaultManaHandler;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record ManaHandlerType<T extends ManaHandler>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaHandlerType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_handler_type"));
    public static final Registry<ManaHandlerType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final ManaHandlerType<DefaultManaHandler> DEFAULT = register("default", DefaultManaHandler.ENDEC);
    public static final ManaHandlerType<AmethystManaHandler> AMETHYST = register("amethyst", AmethystManaHandler.ENDEC);

    private static <T extends ManaHandler> ManaHandlerType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaHandlerType<>(endec));
    }
}

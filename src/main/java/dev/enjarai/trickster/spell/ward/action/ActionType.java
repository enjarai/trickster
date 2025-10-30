package dev.enjarai.trickster.spell.ward.action;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record ActionType<T extends Action<?>>() {
    public static final RegistryKey<Registry<ActionType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("action_type"));
    public static final Registry<ActionType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final ActionType<BreakBlockAction> BREAK_BLOCK = register("break_block");

    private static <T extends Action<?>> ActionType<T> register(String name) {
        return Registry.register(REGISTRY, Trickster.id(name), new ActionType<>());
    }

    public static void register() {
        // init the class :brombeere:
    }
}

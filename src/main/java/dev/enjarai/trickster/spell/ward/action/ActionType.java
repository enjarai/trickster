package dev.enjarai.trickster.spell.ward.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Lifecycle;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Tricks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;

public record ActionType<T extends Action<?>>(Optional<Pattern> pattern) {
    private static final Map<Pattern, ActionType<?>> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<ActionType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("action_type"));
    public static final Registry<ActionType<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<ActionType<?>> add(RegistryKey<ActionType<?>> key, ActionType<?> value, RegistryEntryInfo info) {
            value.pattern().ifPresent(pattern -> {
                if (LOOKUP.containsKey(pattern)) {
                    Trickster.LOGGER.warn(
                            "WARNING: A mod is overriding a warded action that is already defined! This may result in an inability to ward against one of the actions. ({} overrode {})",
                            key.getValue(), getId(LOOKUP.get(pattern))
                    );
                }

                LOOKUP.put(pattern, value);
            });

            return super.add(key, value, info);
        }
    }).buildAndRegister();

    public static final ActionType<BreakBlockAction> BREAK_BLOCK = register("break_block", Tricks.BREAK_BLOCK.getPattern());

    private static <T extends Action<?>> ActionType<T> register(String name) {
        return register(name, Optional.empty());
    }

    private static <T extends Action<?>> ActionType<T> register(String name, Pattern pattern) {
        return register(name, Optional.of(pattern));
    }

    private static <T extends Action<?>> ActionType<T> register(String name, Optional<Pattern> pattern) {
        return Registry.register(REGISTRY, Trickster.id(name), new ActionType<>(pattern));
    }

    @Nullable
    public static ActionType<?> lookup(Pattern pattern) {
        return LOOKUP.get(pattern);
    }

    public static void register() {
        // init the class :brombeere:
    }
}

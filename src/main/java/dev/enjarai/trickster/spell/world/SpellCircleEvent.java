package dev.enjarai.trickster.spell.world;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record SpellCircleEvent(Identifier id, Pattern pattern) {
    private static final Map<Pattern, SpellCircleEvent> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<SpellCircleEvent>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("circle_event"));
    public static final Registry<SpellCircleEvent> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<SpellCircleEvent> add(RegistryKey<SpellCircleEvent> key, SpellCircleEvent value, RegistryEntryInfo info) {
            LOOKUP.put(value.pattern(), value);
            return super.add(key, value, info);
        }
    };

    public static final SpellCircleEvent NONE = register("none", Pattern.EMPTY);
    public static final SpellCircleEvent BREAK_BLOCK = register("break_block", Pattern.of(0, 4, 8, 6, 4, 2, 0));

    private static SpellCircleEvent register(String path, Pattern pattern) {
        var id = Trickster.id(path);
        return Registry.register(REGISTRY, id, new SpellCircleEvent(id, pattern));
    }

    public static void register() {

    }

    @Nullable
    public static SpellCircleEvent lookup(Pattern pattern) {
        return LOOKUP.get(pattern);
    }
}

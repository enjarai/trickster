package dev.enjarai.trickster.spell.world;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.enjarai.trickster.block.SpellCircleBlockEntity.LISTENER_RADIUS;

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
    public static final SpellCircleEvent PLACE_BLOCK = register("place_block", Pattern.of(0, 2, 8, 6, 0));
    public static final SpellCircleEvent ENTITY_MOVE = register("entity_move", Pattern.of(3, 4, 5, 8, 4));
    public static final SpellCircleEvent TICK = register("tick", Pattern.of(0, 3, 6, 7, 8, 5, 2, 1, 0, 4, 5));

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

    public boolean fireAllNearby(ServerWorld world, BlockPos pos, List<Fragment> arguments) {
        var pois = world.getPointOfInterestStorage().getInSquare(
                entry -> entry.value().equals(ModBlocks.SPELL_CIRCLE_POI), pos,
                LISTENER_RADIUS, PointOfInterestStorage.OccupationStatus.ANY
        ).collect(Collectors.toCollection(ArrayList::new));

        if (pois.isEmpty()) {
            return false;
        }

        for (var poi : pois) {
            var entity = world.getBlockEntity(poi.getPos());
            if (entity instanceof SpellCircleBlockEntity circleEntity &&
                    circleEntity.event == this &&
                    circleEntity.callEvent(arguments)) {

                return true;
            }
        }

        return false;
    }
}

package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.ward.Ward;
import dev.enjarai.trickster.spell.ward.action.Action;
import dev.enjarai.trickster.spell.ward.action.Target;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class WardManagerComponent implements CommonTickingComponent, AutoSyncedComponent {
    private static final Endec<Map<UUID, Ward>> WARDS_ENDEC = Endec.map(EndecTomfoolery.UUID, Ward.ENDEC);
    private static final KeyedEndec<Map<UUID, Ward>> KEYED_WARDS_ENDEC = WARDS_ENDEC.keyed("wards", () -> new HashMap<>());

    private final World world;
    private final Map<UUID, Ward> wards = new HashMap<>();

    public WardManagerComponent(World world) {
        this.world = world;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        wards.clear();
        wards.putAll(tag.get(KEYED_WARDS_ENDEC));
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.put(KEYED_WARDS_ENDEC, wards);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        var map = buf.read(WARDS_ENDEC);
        wards.clear();
        wards.putAll(map);
    }

    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(WARDS_ENDEC, wards);
    }

    @Override
    public void tick() {
        var toRemove = new ArrayList<UUID>();

        for (var kv : wards.entrySet()) {
            var ward = kv.getValue();
            ward.tick(world);

            if (!ward.shouldLive(world)) {
                toRemove.add(kv.getKey());
            }
        }

        for (var uuid : toRemove) {
            wards.remove(uuid);
        }
    }

    public UUID put(Ward ward) {
        var uuid = UUID.randomUUID(); // Evelyn I hate doing this but <3
        wards.put(uuid, ward);
        return uuid;
    }

    public Optional<Ward> get(UUID uuid) {
        return Optional.ofNullable(wards.get(uuid));
    }

    public ArrayList<Ward> allThatCanCancel(Action<?> action) {
        var result = new ArrayList<Ward>();

        for (var ward : wards.values()) {
            if (ward.matchTarget(action.target(world)) && ward.matchAction(action.type())) {
                result.add(ward);
            }
        }

        return result;
    }

    // if this returns true, the caller MUST cancel the action
    // failing to do so would result in unexpected drain of the ward's reserves
    public <T extends Target> boolean shouldCancel(Action<T> action) {
        var wards = allThatCanCancel(action);

        if (wards.size() > 0) {
            float cost = action.cost(world);

            for (var ward : wards) {
                ward.drain(world, cost / wards.size());
            }

            return true;
        } else {
            return false;
        }
    }
}

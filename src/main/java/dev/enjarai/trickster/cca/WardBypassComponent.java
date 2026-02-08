package dev.enjarai.trickster.cca;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class WardBypassComponent implements AutoSyncedComponent {
    private static final Endec<Map<UUID, Long>> WARDS_ENDEC = Endec.map(EndecTomfoolery.UUID, Endec.LONG);
    private static final KeyedEndec<Map<UUID, Long>> KEYED_WARDS_ENDEC = WARDS_ENDEC.keyed("wards", HashMap::new);

    private final Map<UUID, Long> wards = new HashMap<>();

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

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(WARDS_ENDEC, wards);
    }

    public void apply(World world, UUID uuid) {
        wards.put(uuid, world.getTime());
    }

    public boolean contains(World world, UUID uuid) {
        var value = wards.get(uuid);
        if (value == null) {
            return false;
        }

        var diff = world.getTime() - value;
        if (diff > 5 * 20) {
            return false;
        }

        return true;
    }
}

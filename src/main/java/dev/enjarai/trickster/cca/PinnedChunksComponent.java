package dev.enjarai.trickster.cca;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;

public class PinnedChunksComponent implements ServerTickingComponent {
    public static final KeyedEndec<Map<Long, Integer>> PINS_ENDEC =
            Endec.map(Endec.LONG, Endec.INT).keyed("pins", Map.of());
    public static final ChunkTicketType<ChunkPos> TICKET_TYPE =
            ChunkTicketType.create("trickster:pinned", Comparator.comparingLong(ChunkPos::toLong), 80);

    private final World world;
    private final Long2IntMap pins = new Long2IntOpenHashMap();

    public PinnedChunksComponent(World world) {
        this.world = world;
    }

    @Override
    public void serverTick() {
        var pinSet = new HashSet<>(pins.keySet());
        pinSet.forEach(chunk -> pins.compute(chunk.longValue(), (c, t) -> t <= 0 ? null : t - 1));
        if (!pins.isEmpty()) {
            ((ServerWorld) world).resetIdleTimeout();
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        pins.clear();
        pins.putAll(tag.get(PINS_ENDEC));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(PINS_ENDEC, pins);
    }

    /**
     * Pin a specific chunk for 80 ticks.
     */
    public void pinChunk(ChunkPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) {
            throw new IllegalStateException("Can only pin chunks on logical server");
        }

        pins.put(pos.toLong(), 80);
        serverWorld.getChunkManager().addTicket(TICKET_TYPE, pos, 2, pos);
    }

    public boolean isPinned(ChunkPos pos) {
        return pins.containsKey(pos.toLong());
    }

    public void pinThemAll() {
        // This means a server reboot will pin chunks for slightly longer than normal,
        // but that shouldn't be an issue in practice.
        pins.keySet().forEach(chunk -> {
            var pos = new ChunkPos(chunk);
            ((ServerWorld) world).getChunkManager().addTicket(TICKET_TYPE, pos, 2, pos);
        });
    }
}

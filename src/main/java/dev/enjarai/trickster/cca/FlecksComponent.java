package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.fleck.Fleck;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.List;
import java.util.Map;

public class FlecksComponent implements ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    /*
     * TODO: make it so the client wont try to render too many flecks at once and crash (probably not a change in this class)
     * hash fleck id with spell source hash (e.g player uuid), to prevent collisions (only if it becomes annoying)
     * */

    private static final Endec<Map<Integer, FleckEntry>> FLECKS_ENDEC = Endec.map(Endec.INT, FleckEntry.ENDEC);
    private static final Integer STAY_FOR_TICKS = 20;

    private final PlayerEntity player;
    private final Int2ObjectMap<FleckEntry> flecks = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<FleckEntry> clientFlecks = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<FleckEntry> prevClientFlecks = new Int2ObjectOpenHashMap<>();
    private boolean dirty;

    public FlecksComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        //nop like bars
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        //nop like bars
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        var map = buf.read(FLECKS_ENDEC);
        flecks.putAll(map);
    }

    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(FLECKS_ENDEC, flecks);
    }

    public void addFleck(int id, Fleck fleck) {
        flecks.put(id, new FleckEntry(fleck, STAY_FOR_TICKS));
        markDirty();
    }

    public List<FleckPair> getRenderFlecks() {
       return clientFlecks.int2ObjectEntrySet().stream().map(e -> {
           var current = e.getValue();
           var old = prevClientFlecks.get(e.getIntKey());
           return new FleckPair(e.getIntKey(), current.fleck(), old == null ? null : old.fleck());
       }).toList();
    }

    private void markDirty() {
        this.dirty = true;
    }

    @Override
    public void clientTick() {
        prevClientFlecks.clear();
        prevClientFlecks.putAll(clientFlecks);
        clientFlecks.clear();
        clientFlecks.putAll(flecks);

        commonTick();
    }

    private void commonTick() {
        for (var iterator = flecks.int2ObjectEntrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var life = entry.getValue().life();
            var fleck = entry.getValue().fleck;
            if (--life >= 0) {
                entry.setValue(new FleckEntry(fleck, life));
            } else {
                iterator.remove();
            }
        }
    }

    @Override
    public void serverTick() {
        if (dirty) {
            ModEntityComponents.FLECKS.sync(player);
            dirty = false;
        }

        commonTick();
    }

    private record FleckEntry(Fleck fleck, int life) {
        public static final Endec<FleckEntry> ENDEC = StructEndecBuilder.of(
                Fleck.ENDEC.fieldOf("fleck", FleckEntry::fleck),
                Endec.INT.fieldOf("life", FleckEntry::life),
                FleckEntry::new
        );
    }

    public record FleckPair(int id, Fleck current, @Nullable Fleck old) {}
}


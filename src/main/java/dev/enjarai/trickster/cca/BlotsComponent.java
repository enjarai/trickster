package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.spell.blot.Blot;
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

public class BlotsComponent implements ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    private static final Endec<Map<Integer, BlotEntry>> BLOTS_ENDEC = Endec.map(Endec.INT, BlotEntry.ENDEC);
    private static final Integer STAY_FOR_TICKS = 20;

    private final PlayerEntity player;
    private final Int2ObjectMap<BlotEntry> blots = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BlotEntry> clientBlots = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BlotEntry> prevClientBlots = new Int2ObjectOpenHashMap<>();
    private boolean dirty;

    public BlotsComponent(PlayerEntity player) {
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
        var map = buf.read(BLOTS_ENDEC);
        blots.putAll(map);
    }

    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(BLOTS_ENDEC, blots);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    public void addBlot(int id, Blot Blot) {
        blots.put(id, new BlotEntry(Blot, STAY_FOR_TICKS));
        markDirty();
    }

    public List<BlotPair> getRenderBlots() {
       return clientBlots.int2ObjectEntrySet().stream().map(e -> {
           var current = e.getValue();
           var old = prevClientBlots.get(e.getIntKey());
           return new BlotPair(e.getIntKey(), current.blot(), old == null ? null : old.blot());
       }).toList();
    }

    private void markDirty() {
        this.dirty = true;
    }

    @Override
    public void clientTick() {
        prevClientBlots.clear();
        prevClientBlots.putAll(clientBlots);
        clientBlots.clear();
        clientBlots.putAll(blots);

        commonTick();
    }

    private void commonTick() {
        for (var iterator = blots.int2ObjectEntrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var life = entry.getValue().life();
            var blot = entry.getValue().blot();
            if (--life >= 0) {
                entry.setValue(new BlotEntry(blot, life));
            } else {
                iterator.remove();
            }
        }
    }

    @Override
    public void serverTick() {
        if (dirty) {
            ModEntityComponents.BLOTS.sync(player);
            dirty = false;
        }

        commonTick();
    }

    private record BlotEntry(Blot blot, int life) {
        public static final Endec<BlotEntry> ENDEC = StructEndecBuilder.of(
                Blot.ENDEC.fieldOf("blot", BlotEntry::blot),
                Endec.INT.fieldOf("life", BlotEntry::life),
                BlotEntry::new
        );
    }

    public record BlotPair(int id, Blot current, @Nullable Blot old) {}
}


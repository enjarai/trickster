package dev.enjarai.trickster.cca;

import com.mojang.datafixers.util.Pair;
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
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Map;

public class FlecksComponent implements ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    /*
     * TODO: make it so the client wont try to render too many flecks at once and crash (probably not a change in this class)
     * hash fleck id with spell source hash (e.g player uuid), to prevent collisions (only if it becomes annoying)
     * */

    public static final Endec<Pair<Fleck, Integer>> PAIR_ENDEC = StructEndecBuilder.of(Fleck.ENDEC.fieldOf("first", Pair::getFirst), Endec.INT.fieldOf("second", Pair::getSecond), Pair::new);

    public static final Endec<Map<Integer, Pair<Fleck, Integer>>> FLECKS_ENDEC = Endec.map(Endec.INT, PAIR_ENDEC);
    private static final Integer STAY_FOR_TICKS = 20;

    private final PlayerEntity player;
    private final Int2ObjectMap<Pair<Fleck, Integer>> flecks = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Pair<Fleck, Integer>> oldFlecks = new Int2ObjectOpenHashMap<>();
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
        map.forEach((id,pair) -> {
            var oldFleck = flecks.getOrDefault((int) id, pair);
            if (pair.getFirst().type() == oldFleck.getFirst().type()) {
                oldFlecks.putIfAbsent(id, oldFleck);
            }
        }); //once per client tick, and if the type matches update old flecks with the changes
        flecks.putAll(map);
    }

    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(FLECKS_ENDEC, flecks);
    }

    public void addFleck(int id, Fleck fleck) {
        flecks.put(id, Pair.of(fleck, STAY_FOR_TICKS));
        markDirty();
    }

    public Int2ObjectMap<Pair<Fleck, Integer>> getFlecks() {
        return this.flecks;
    }

    public Int2ObjectMap<Pair<Fleck, Integer>> getOldFlecks(){
        return this.oldFlecks;
    }

    private void markDirty() {
        this.dirty = true;
    }

    @Override
    public void clientTick() {
        flecks.keySet().forEach(key -> flecks.compute(key, FlecksComponent::update)); //intellij complains but it being null is the point
        oldFlecks.clear();
    }

    private static Pair<Fleck, Integer> update(int key, Pair<Fleck, Integer> pair) {
        Integer life = pair.getSecond();
        Fleck fleck = pair.getFirst();
        return (--life >= 0) ? Pair.of(fleck, life) : null; // if its life is less than zero after decreasing, remove it from the map
    }

    @Override
    public void serverTick() {
        if (dirty) {
            ModEntityCumponents.FLECKS.sync(player);
            dirty = false;
        }
    }
}


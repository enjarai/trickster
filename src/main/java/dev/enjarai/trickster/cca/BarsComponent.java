package dev.enjarai.trickster.cca;

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
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Map;

public class BarsComponent implements ServerTickingComponent, AutoSyncedComponent {
    public static final int STAY_FOR_TICKS = 100;
    public static final Endec<Map<Integer, Bar>> BARS_ENDEC = Endec.map(Endec.INT, Bar.ENDEC);

    private final PlayerEntity player;
    private final Int2ObjectMap<Bar> bars = new Int2ObjectOpenHashMap<>();
    private int lastBarsHashcode;

    public BarsComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        for (var iterator = bars.int2ObjectEntrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();

            if (entry.getValue().age >= STAY_FOR_TICKS) {
                iterator.remove();
            } else {
                entry.getValue().age++;
            }
        }
        ModEntityCumponents.BARS.sync(player);
    }

    public Int2ObjectMap<Bar> getBars() {
        return bars;
    }

    public void setBar(int id, double fill) {
        var bar = bars.computeIfAbsent(id, i -> new Bar(fill));
        bar.fill = fill;
        bar.age = 0;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        // No-op
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        // No-op
        // No need to permanently save this information at all
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (player != this.player) return false;

        var hash = bars.hashCode();
        if (hash != lastBarsHashcode) {
            lastBarsHashcode = hash;
            return true;
        }
        return false;
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        bars.clear();
        bars.putAll(buf.read(BARS_ENDEC));
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(BARS_ENDEC, bars);
    }

    public static class Bar {
        public static final Endec<Bar> ENDEC = StructEndecBuilder.of(
                Endec.DOUBLE.fieldOf("fill", b -> b.fill),
                Bar::new
        );

        public double fill;
        public int age;

        public Bar(double fill) {
            this(fill, 0);
        }

        public Bar(double fill, int age) {
            this.fill = fill;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bar bar = (Bar) o;
            return Double.compare(fill, bar.fill) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(fill) + 1;
        }
    }
}

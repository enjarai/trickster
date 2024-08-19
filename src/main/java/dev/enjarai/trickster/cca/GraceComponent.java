package dev.enjarai.trickster.cca;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Map;
import java.util.function.Function;

public class GraceComponent implements ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    private final LivingEntity entity;

    private final Object2IntMap<String> graces = new Object2IntOpenHashMap<>();

    private final Endec<Map<String, Integer>> ENDEC = Endec.map(Function.identity(), Function.identity(), Endec.INT);
    private final KeyedEndec<Map<String, Integer>> KEYED_ENDEC = ENDEC.keyed("graces", Map.of());

    public GraceComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void serverTick() {
        tick();
    }

    @Override
    public void clientTick() {
        tick();
    }

    public void tick() {
        for (var iterator = graces.object2IntEntrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var value = entry.getIntValue();
            if (value > 0) {
                entry.setValue(value - 1);
            } else {
                iterator.remove();
            }
        }
    }

    public boolean isInGrace(String grace) {
        return graces.containsKey(grace);
    }

    public void triggerGrace(String grace, int ticks) {
        graces.put(grace, ticks);
        ModEntityCumponents.GRACE.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        graces.clear();
        graces.putAll(tag.get(KEYED_ENDEC));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(KEYED_ENDEC, graces);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        graces.clear();
        graces.putAll(buf.read(ENDEC));
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(ENDEC, graces);
    }
}

package dev.enjarai.trickster.cca;

import com.mojang.serialization.DataResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.mana.ManaPool;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CasterComponent implements ServerTickingComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private SpellExecutionManager executionManager;
    private Int2ObjectMap<RunningSpellData> runningSpellData = new Int2ObjectOpenHashMap<>();
    private int lastSentSpellDataHash;
    private int wait;

    public static final Endec<Map<Integer, RunningSpellData>> SPELL_DATA_ENDEC =
            Endec.map(Endec.INT, ReflectiveEndecBuilder.SHARED_INSTANCE.get(RunningSpellData.class));

    public CasterComponent(PlayerEntity player) {
        this.player = player;
        // TODO: make capacity of execution manager an attribute
        this.executionManager = new SpellExecutionManager(5);

        if (!player.getWorld().isClient()) {
            this.executionManager.setSource(new PlayerSpellSource((ServerPlayerEntity) player));
        }
    }

    @Override
    public void serverTick() {
        if (wait > 0) {
            wait--;
            return;
        }

        runningSpellData.clear();
        executionManager.tick((index, executor) ->
                runningSpellData.put(index, new RunningSpellData(executor.getLastRunExecutions())));
        ModEntityCumponents.CASTER.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DataResult<SpellExecutionManager> result = SpellExecutionManager.CODEC.parse(NbtOps.INSTANCE, tag.get("manager"));

        if (result.hasResultOrPartial())
            executionManager = result.resultOrPartial().orElseThrow();

        executionManager.setSource(new PlayerSpellSource((ServerPlayerEntity) player));
        waitTicks(20);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DataResult<NbtElement> result = SpellExecutionManager.CODEC.encodeStart(NbtOps.INSTANCE, executionManager);
        tag.put("manager", result.result().orElseThrow());
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (player != this.player) return false;

        var hash = runningSpellData.hashCode();
        if (hash != lastSentSpellDataHash) {
            lastSentSpellDataHash = hash;
            return true;
        }
        return false;
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        runningSpellData.clear();
        runningSpellData.putAll(buf.read(SPELL_DATA_ENDEC));
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.write(SPELL_DATA_ENDEC, runningSpellData);
    }

    public void queue(SpellPart spell, List<Fragment> arguments) {
        executionManager.queue(spell, arguments);
    }

    public void queue(SpellPart spell, List<Fragment> arguments, ManaPool poolOverride) {
        executionManager.queue(spell, arguments, poolOverride);
    }

    public void killAll() {
        executionManager.killAll();
    }

    public void kill(int index) {
        executionManager.kill(index);
    }

    public void waitTicks(int ticks) {
        wait += ticks;
    }

    public Int2ObjectMap<RunningSpellData> getRunningSpellData() {
        return runningSpellData;
    }

    public record RunningSpellData(int executionsLastTick) {
        @Override
        public int hashCode() {
            return Objects.hash(1, executionsLastTick);
        }
    }
}

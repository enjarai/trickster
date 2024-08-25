package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.SpellQueueResult;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.mana.ManaPool;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CasterComponent implements ServerTickingComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private SpellExecutionManager executionManager;
    private Int2ObjectMap<RunningSpellData> runningSpellData = new Int2ObjectOpenHashMap<>();
    private int lastSentSpellDataHash;
    private int wait;

    public static final Endec<Map<Integer, RunningSpellData>> SPELL_DATA_ENDEC =
            Endec.map(Endec.INT, StructEndecBuilder.of(
                    Endec.INT.fieldOf("executions_last_tick", RunningSpellData::executionsLastTick),
                    Endec.BOOLEAN.fieldOf("errored", RunningSpellData::errored),
                    MinecraftEndecs.TEXT.optionalOf().optionalFieldOf("message", RunningSpellData::message, Optional.empty()),
                    RunningSpellData::new
            ));
    public static final KeyedEndec<SpellExecutionManager> EXECUTION_MANAGER_ENDEC =
            SpellExecutionManager.ENDEC.keyed("manager", () -> new SpellExecutionManager(5));

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
        executionManager.tick(this::afterExecutorTick, this::completeExecutor, this::executorError);
        ModEntityCumponents.CASTER.sync(player);
    }

    private void afterExecutorTick(int index, SpellExecutor executor) {
        Text message = null;
        boolean errored = false;
        if (executor instanceof ErroredSpellExecutor error) {
            message = error.errorMessage();
            errored = true;
        }
        runningSpellData.put(index, new RunningSpellData(
                executor.getLastRunExecutions(), errored, Optional.ofNullable(message)));
    }

    private void completeExecutor(int index, SpellExecutor executor) {
        playCastSound(1.2f, 0.1f);
    }

    private void executorError(int index, SpellExecutor executor) {
        playCastSound(0.5f, 0.1f);
    }

    private void playCastSound(float startPitch, float pitchRange) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.getServerWorld().playSoundFromEntity(
                    null, serverPlayer, ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(startPitch, pitchRange));
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        executionManager = tag.get(EXECUTION_MANAGER_ENDEC);
        executionManager.setSource(new PlayerSpellSource((ServerPlayerEntity) player));
        waitTicks(20);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(EXECUTION_MANAGER_ENDEC, executionManager);
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

    public boolean queueSpell(SpellPart spell, List<Fragment> arguments) {
        playCastSound(0.8f, 0.1f);
        return executionManager.queue(spell, arguments);
    }

    public SpellQueueResult queueSpellAndCast(SpellPart spell, List<Fragment> arguments, Optional<ManaPool> poolOverride) {
        playCastSound(0.8f, 0.1f);
        return executionManager.queueAndCast(spell, arguments, poolOverride);
    }

    public void killAll() {
        executionManager.killAll();
    }

    public void kill(int index) {
        executionManager.kill(index);
//        playCastSound(0.6f, 0.1f);
    }

    public SpellExecutionManager getExecutionManager() {
        return executionManager;
    }

    public void waitTicks(int ticks) {
        wait += ticks;
    }

    public Int2ObjectMap<RunningSpellData> getRunningSpellData() {
        return runningSpellData;
    }

    public record RunningSpellData(int executionsLastTick, boolean errored, Optional<Text> message) {
        @Override
        public int hashCode() {
            var result = Objects.hash(1, executionsLastTick);
            result = 31 * result + Boolean.hashCode(errored);
            result = 31 * result + Objects.hashCode(message);
            return result;
        }
    }
}

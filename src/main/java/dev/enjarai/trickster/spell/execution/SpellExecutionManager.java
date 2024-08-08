package dev.enjarai.trickster.spell.execution;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NaNBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpellExecutionManager {
    public static final StructEndec<SpellExecutionManager> ENDEC = StructEndecBuilder.of(
            Endec.INT.optionalFieldOf("capacity", e -> e.capacity, 5),
            Endec.map(Object::toString, Integer::parseInt, SpellExecutor.ENDEC).fieldOf("spells", e -> e.spells),
            SpellExecutionManager::new
    );

    // NOT final, we want to be able to change this perchance
    private int capacity;
    private SpellSource source;
    private final Int2ObjectMap<SpellExecutor> spells;

    private SpellExecutionManager(int initialCapacity, Map<Integer, SpellExecutor> spells) {
        this(initialCapacity);
        this.spells.putAll(spells);
    }

    public SpellExecutionManager(int initialCapacity) {
        this.spells = new Int2ObjectOpenHashMap<>(initialCapacity);
        this.capacity = initialCapacity;
    }

    public boolean queue(SpellPart spell, List<Fragment> arguments) {
        return queue(new DefaultSpellExecutor(spell, arguments));
    }

    public SpellQueueResult queueAndCast(SpellPart spell, List<Fragment> arguments, ManaPool poolOverride) {
        var executor = new DefaultSpellExecutor(spell, new ExecutionState(arguments, poolOverride));
        boolean queued = queue(executor);

        if (queued) {
            for (var iterator = spells.int2ObjectEntrySet().iterator(); iterator.hasNext();) {
                var entry = iterator.next();

                if (entry.getValue() == executor) {
                    AtomicBoolean isDone = new AtomicBoolean(true);
                    tryRun(entry,
                            (index, executor1) -> isDone.set(false),
                            (index, executor2) -> iterator.remove(),
                            (index, executor3) -> { });
                    return new SpellQueueResult(isDone.get()
                            ? SpellQueueResult.Type.QUEUED_DONE
                            : SpellQueueResult.Type.QUEUED_STILL_RUNNING, executor.getCurrentState());
                }
            }
        }

        return new SpellQueueResult(SpellQueueResult.Type.NOT_QUEUED, executor.getCurrentState());
    }

    public boolean queue(SpellExecutor executor) {
        for (int i = 0; i < capacity; i++) {
            if (spells.putIfAbsent(i, executor) == null) {
                return true;
            }
        }
        for (int i = 0; i < capacity; i++) {
            if (spells.get(i) instanceof ErroredSpellExecutor) {
                spells.put(i, executor);
                return true;
            }
        }
        return false;
    }

    public void tick(ExecutorCallback tickCallback, ExecutorCallback completeCallback, ExecutorCallback errorCallback) {
        if (source == null)
            return;

        for (var iterator = spells.int2ObjectEntrySet().iterator(); iterator.hasNext();) {
            var entry = iterator.next();
            tryRun(entry, tickCallback, (index, executor) -> {
                iterator.remove();
                completeCallback.callTheBack(index, executor);
            }, errorCallback);
        }
    }

    /**
     * Attempts to run the given entry's SpellExecutor.
     * @param entry
     * @param tickCallback
     * @param completeCallback
     * @param errorCallback
     * @return whether the spell has finished running or not. Blunders and normal completion return true, otherwise returns false.
     */
    private boolean tryRun(Int2ObjectMap.Entry<SpellExecutor> entry, ExecutorCallback tickCallback, ExecutorCallback completeCallback, ExecutorCallback errorCallback) {
        var spell = entry.getValue();

        try {
            if (spell.run(source).isEmpty()) {
                tickCallback.callTheBack(entry.getIntKey(), spell);
                return false;
            } else {
                completeCallback.callTheBack(entry.getIntKey(), spell);
                return true;
            }
        } catch (BlunderException blunder) {
            var message = blunder.createMessage()
                    .append(" (").append(spell.getCurrentState().formatStackTrace()).append(")");

            if (blunder instanceof NaNBlunder)
                source.getPlayer().ifPresent(ModCriteria.NAN_NUMBER::trigger);

            entry.setValue(new ErroredSpellExecutor(message));
            source.getPlayer().ifPresent(player -> player.sendMessage(message));
            errorCallback.callTheBack(entry.getIntKey(), spell);
        } catch (Exception e) {
            var message = Text.literal("Uncaught exception in spell: " + e.getMessage())
                    .append(" (").append(spell.getCurrentState().formatStackTrace()).append(")");

            entry.setValue(new ErroredSpellExecutor(message));
            source.getPlayer().ifPresent(player -> player.sendMessage(message));
            errorCallback.callTheBack(entry.getIntKey(), spell);
        }

        return true;
    }

    public void setSource(SpellSource source) {
        this.source = source;
    }

    public void killAll() {
        spells.clear();
    }

    public void kill(int index) {
        spells.remove(index);
    }

    public interface ExecutorCallback {
        void callTheBack(int index, SpellExecutor executor);
    }
}

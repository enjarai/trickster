package dev.enjarai.trickster.spell.execution;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NaNBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerSpellExecutionManager implements SpellExecutionManager {
    public static final StructEndec<PlayerSpellExecutionManager> ENDEC = StructEndecBuilder.of(
            Endec.INT.optionalFieldOf("capacity", e -> e.capacity, 5),
            Endec.map(Object::toString, Integer::parseInt, SpellExecutor.ENDEC).fieldOf("spells", e -> e.spells),
            PlayerSpellExecutionManager::new
    );

    // NOT final, we want to be able to change this perchance
    private int capacity;
    private final Int2ObjectMap<SpellExecutor> spells;

    private PlayerSpellExecutionManager(int initialCapacity, Map<Integer, SpellExecutor> spells) {
        this(initialCapacity);
        this.spells.putAll(spells);
    }

    public PlayerSpellExecutionManager(int initialCapacity) {
        this.spells = new Int2ObjectOpenHashMap<>(initialCapacity);
        this.capacity = initialCapacity;
    }

    public SpellQueueResult queueAndCast(SpellSource source, SpellPart spell, List<Fragment> arguments, Optional<MutableManaPool> poolOverride) {
        var executor = new DefaultSpellExecutor(spell, poolOverride.flatMap(pool -> Optional.of(new ExecutionState(arguments, pool))).orElse(new ExecutionState(arguments)));
        boolean queued = queue(executor);

        if (queued) {
            for (var iterator = spells.int2ObjectEntrySet().iterator(); iterator.hasNext();) {
                var entry = iterator.next();

                if (entry.getValue() == executor) {
                    AtomicBoolean isDone = new AtomicBoolean(true);
                    tryRun(source, entry,
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

    @Override
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

    public void tick(SpellSource source, ExecutorCallback tickCallback, ExecutorCallback completeCallback, ExecutorCallback errorCallback) {
        for (var iterator = spells.int2ObjectEntrySet().iterator(); iterator.hasNext();) {
            var entry = iterator.next();
            tryRun(source, entry, tickCallback, (index, executor) -> {
                iterator.remove();
                completeCallback.callTheBack(index, executor);
            }, errorCallback);
        }
    }

    /**
     * Attempts to run the given entry's SpellExecutor.
     * @param source TODO
     * @param entry TODO
     * @param tickCallback TODO
     * @param completeCallback TODO
     * @param errorCallback TODO
     * @return whether the spell has finished running or not. Blunders and normal completion return true, otherwise returns false.
     */
    private boolean tryRun(SpellSource source, Int2ObjectMap.Entry<SpellExecutor> entry, ExecutorCallback tickCallback, ExecutorCallback completeCallback, ExecutorCallback errorCallback) {
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

    @Override
    public void killAll() {
        spells.clear();
    }

    @Override
    public void kill(int index) {
        spells.remove(index);
    }

    public interface ExecutorCallback {
        void callTheBack(int index, SpellExecutor executor);
    }
}

package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ExecutionLimitReachedBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

import java.util.Optional;

public interface SpellExecutor {
    @SuppressWarnings("unchecked")
    StructEndec<SpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<SpellExecutor>) Endec.dispatchedStruct(
            SpellExecutorType::endec, SpellExecutor::type, MinecraftEndecs.ofRegistry(SpellExecutorType.REGISTRY)));

    SpellExecutorType<?> type();

    SpellPart spell();

    /**
     * Attempts to execute the spell within a single tick, throws ExecutionLimitReachedBlunder if single-tick execution is not feasible.
     * <p>
     * Before this function throws, it will append the additional spell stacktrace to the stacktrace in the provided context.
     *
     * @return the spell's result.
     * @throws BlunderException
     */
    default Fragment singleTickRun(SpellContext context) throws BlunderException {
        try {
            return run(context.source()).orElseThrow(ExecutionLimitReachedBlunder::new);
        } catch (Exception e) {
            context.state().getStacktrace().clear();
            context.state().getStacktrace().addAll(getDeepestState().getStacktrace());
            throw e;
        }
    }

    /**
     * Attempts to execute the spell within a single tick, throws ExecutionLimitReachedBlunder if single-tick execution is not feasible.
     *
     * @return the spell's result.
     * @throws BlunderException
     */
    default Fragment singleTickRun(SpellSource source) throws BlunderException {
        return run(source).orElseThrow(ExecutionLimitReachedBlunder::new);
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    default Optional<Fragment> run(SpellSource source) throws BlunderException {
        return run(source, new TickData());
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException;

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    Optional<Fragment> run(SpellContext ctx) throws BlunderException;

    /**
     * @return the spell's executions in the last tick, or its child's if applicable.
     */
    int getLastRunExecutions();

    /**
     * @return the spell's ExecutionState, or its child's if applicable.
     */
    ExecutionState getDeepestState();
}

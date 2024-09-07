package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.ExecutionLimitReachedBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

import java.util.Optional;
import java.util.Stack;

public interface SpellExecutor {
    @SuppressWarnings("unchecked")
    StructEndec<SpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<SpellExecutor>) Endec.dispatchedStruct(
            SpellExecutorType::endec, SpellExecutor::type, MinecraftEndecs.ofRegistry(SpellExecutorType.REGISTRY)));

    SpellExecutorType<?> type();

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
            context.executionState().getStacktrace().clear();
            context.executionState().getStacktrace().addAll(getCurrentState().getStacktrace());
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
        return run(source, new ExecutionCounter());
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    Optional<Fragment> run(SpellContext ctx, ExecutionCounter executions) throws BlunderException;

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    default Optional<Fragment> run(SpellSource source, ExecutionCounter executions) throws BlunderException {
        return run(new SpellContext(source, getCurrentState()), executions);
    }

    int getLastRunExecutions();

    ExecutionState getCurrentState();

    // made non-recursive by @ArkoSammy12
    default Stack<SpellInstruction> flattenNode(SpellPart head) {
        Stack<SpellInstruction> instructions = new Stack<>();
        Stack<SpellPart> headStack = new Stack<>();
        Stack<Integer> indexStack = new Stack<>();

        headStack.push(head);
        indexStack.push(-1);

        while (!headStack.isEmpty()) {
            SpellPart currentNode = headStack.peek();
            int currentIndex = indexStack.pop();

            if (currentIndex == -1) {
                instructions.push(new ExitScopeInstruction());
                instructions.push(currentNode.glyph);
            }

            currentIndex++;

            if (currentIndex < currentNode.subParts.size()) {
                headStack.push(currentNode.subParts.reversed().get(currentIndex));
                indexStack.push(currentIndex);
                indexStack.push(-1);
            } else {
                headStack.pop();
                instructions.push(new EnterScopeInstruction());
            }
        }

        return instructions;
    }

    class ExecutionCounter {
        int executions;

        public void increment() {
            executions++;
        }

        public int getExecutions() {
            return executions;
        }

        public boolean isLimitReached() {
            return executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick();
        }
    }
}

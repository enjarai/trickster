package dev.enjarai.trickster.spell.execution.executor;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class FoldingSpellExecutor implements SpellExecutor {
    public static final StructEndec<FoldingSpellExecutor> ENDEC = StructEndecBuilder.of(
            ExecutionState.ENDEC.fieldOf("state", e -> e.state),
            SpellPart.ENDEC.fieldOf("executable", e -> e.executable),
            Fragment.ENDEC.fieldOf("last_result", e -> e.lastResult),
            EndecTomfoolery.stackOf(Fragment.ENDEC).fieldOf("values", e -> e.values),
            EndecTomfoolery.stackOf(Fragment.ENDEC).fieldOf("keys", e -> e.keys),
            Fragment.ENDEC.fieldOf("previous", e -> e.previous),
            EndecTomfoolery.forcedSafeOptionalOf(SpellExecutor.INTERNAL_ENDEC).fieldOf("child", e -> e.child),
            FoldingSpellExecutor::new
    );

    private final ExecutionState state;
    private final SpellPart executable;
    private final Stack<Fragment> values;
    private final Stack<Fragment> keys;
    private final Fragment previous;
    private Optional<SpellExecutor> child;
    private int lastRunExecutions;
    private Fragment lastResult;

    private FoldingSpellExecutor(ExecutionState state, SpellPart executable, Fragment lastResult, Stack<Fragment> values, Stack<Fragment> keys, Fragment previous, Optional<SpellExecutor> child) {
        this.state = state;
        this.executable = executable;
        this.lastResult = lastResult;
        this.values = values;
        this.keys = keys;
        this.previous = previous;
        this.child = child;
    }

    public FoldingSpellExecutor(SpellContext ctx, SpellPart executable, Fragment result, Stack<Fragment> values, Stack<Fragment> keys, Fragment previous) {
        this(ctx.state().recurseOrThrow(List.of()), executable, result, values, keys, previous, Optional.empty());

        if (values.size() != keys.size())
            throw new IllegalStateException("FoldingSpellExecutor requires that the `values` and `keys` stack be of equal length!");
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.FOLDING;
    }

    @Override
    public SpellPart spell() {
        return executable;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        return run(new SpellContext(state, source, data));
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent()) {
            var result = runChild(ctx);

            if (result.isEmpty())
                return result;
        }

        int size = values.size();

        for (int i = 0; i < size; i++) {
            if (ctx.data().isExecutionLimitReached()) {
                return Optional.empty();
            }

            child = Optional.of(
                    new DefaultSpellExecutor(
                            executable,
                            state.recurseOrThrow(
                                    List.of(
                                            lastResult,
                                            values.pop(),
                                            keys.pop(),
                                            previous
                                    )
                            )
                    )
            );

            var result = runChild(ctx);

            if (result.isEmpty())
                return result;

            ctx.data().incrementExecutions();
            lastRunExecutions = ctx.data().getExecutions();
        }

        return Optional.of(lastResult);
    }

    private Optional<Fragment> runChild(SpellContext ctx) {
        var result = child.flatMap(c -> c.run(ctx.source(), ctx.data()));

        if (result.isPresent()) {
            lastResult = result.get();
            child = Optional.empty();
        }

        return result;
    }

    @Override
    public int getLastRunExecutions() {
        return child.map(SpellExecutor::getLastRunExecutions).orElse(lastRunExecutions);
    }

    @Override
    public ExecutionState getDeepestState() {
        return child.map(SpellExecutor::getDeepestState).orElse(state);
    }
}

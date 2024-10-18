package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class ListFoldingSpellExecutor implements SpellExecutor {
    public static final StructEndec<ListFoldingSpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> StructEndecBuilder.of(
            ExecutionState.ENDEC.fieldOf("state", executor -> executor.state),
            SpellPart.ENDEC.fieldOf("executable", executor -> executor.executable),
            ListFragment.ENDEC.fieldOf("list", executor -> executor.list),
            Fragment.ENDEC.listOf().xmap((l) -> {
                var s = new Stack<Fragment>();
                s.addAll(l);
                return s;
            }, ArrayList::new).fieldOf("elements", executor -> executor.elements),
            EndecTomfoolery.safeOptionalOf(SpellExecutor.ENDEC).optionalFieldOf("child", executor -> executor.child, Optional.empty()),
            Fragment.ENDEC.fieldOf("last", executor -> executor.last),
            ListFoldingSpellExecutor::new
    ));

    protected final ExecutionState state;
    protected final SpellPart executable;
    protected final ListFragment list;
    protected final Stack<Fragment> elements;
    protected Optional<SpellExecutor> child = Optional.empty();
    protected Fragment last;
    protected int lastRunExecutions;

    protected ListFoldingSpellExecutor(ExecutionState state,
                                   SpellPart executable,
                                   ListFragment list,
                                   Stack<Fragment> elements,
                                   Optional<SpellExecutor> child,
                                   Fragment last) {
        this.state = state.recurseOrThrow(List.of());
        this.executable = executable;
        this.list = list;
        this.elements = elements;
        this.child = child;
        this.last = last;
    }

    public ListFoldingSpellExecutor(SpellContext ctx, SpellPart executable, ListFragment list, Fragment initial) {
        this(ctx.executionState(), executable, list, new Stack<>(), Optional.empty(), initial);
        this.elements.addAll(list.fragments().reversed());
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.LIST_FOLDING;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, ExecutionCounter executions) throws BlunderException {
        return run(new SpellContext(source, state), executions);
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx, ExecutionCounter executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent()) {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        int size = elements.size();

        for (int i = 0; i < size; i++) {
            if (executions.isLimitReached()) {
                return Optional.empty();
            }

            child = Optional.of(
                    new DefaultSpellExecutor(
                            executable,
                            state.recurseOrThrow(List.of(
                                    last,
                                    elements.pop(),
                                    new NumberFragment(list.fragments().size() - elements.size() - 1),
                                    list
                            ))
                    )
            );

            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;

            executions.increment();
            lastRunExecutions = executions.getExecutions();
        }

        return Optional.of(last);
    }

    protected Optional<Fragment> runChild(SpellContext ctx, ExecutionCounter executions) {
        var result = child.flatMap(c -> c.run(ctx.source(), executions));

        if (result.isPresent()) {
            last = result.get();
            state.syncLinksFrom(child.get().getCurrentState());
            child = Optional.empty();
        }

        return result;
    }

    @Override
    public int getLastRunExecutions() {
        return child.map(SpellExecutor::getLastRunExecutions).orElse(lastRunExecutions);
    }

    @Override
    public ExecutionState getCurrentState() {
        return child.map(SpellExecutor::getCurrentState).orElse(state);
    }
}

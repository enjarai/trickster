package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class FoldingSpellExecutor implements SpellExecutor {
    public static final StructEndec<FoldingSpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> StructEndecBuilder.of(
            ExecutionState.ENDEC.fieldOf("state", executor -> executor.parentState),
            SpellPart.ENDEC.fieldOf("executable", executor -> executor.executable),
            ListFragment.ENDEC.fieldOf("list", executor -> executor.list),
            Fragment.ENDEC.listOf().xmap((l) -> {
                var s = new Stack<Fragment>();
                s.addAll(l/*.reversed()*/); /*TODO*/
                return s;
            }, ArrayList::new).fieldOf("elements", executor -> executor.elements),
            SpellExecutor.ENDEC.optionalOf().optionalFieldOf("child", executor -> executor.child, Optional.empty()),
            Fragment.ENDEC.fieldOf("last", executor -> executor.last),
            FoldingSpellExecutor::new
    ));

    protected final ExecutionState parentState;
    protected final SpellPart executable;
    protected final ListFragment list;
    protected final Stack<Fragment> elements;
    protected Optional<SpellExecutor> child = Optional.empty();
    protected Fragment last;
    protected int lastRunExecutions;

    protected FoldingSpellExecutor(ExecutionState parentState,
                                   SpellPart executable,
                                   ListFragment list,
                                   Stack<Fragment> elements,
                                   Optional<SpellExecutor> child,
                                   Fragment last) {
        this.parentState = parentState;
        this.executable = executable;
        this.list = list;
        this.elements = elements;
        this.child = child;
        this.last = last;
    }

    public FoldingSpellExecutor(SpellContext ctx, SpellPart executable, ListFragment list, Fragment initial) {
        this(ctx.executionState(), executable, list, new Stack<>(), Optional.empty(), initial);
        this.elements.addAll(list.fragments().reversed());
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.FOLDING;
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
                            ctx.executionState()
                                    .recurseOrThrow(List.of(
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
            parentState.syncLinksFrom(child.get().getCurrentState());
            last = result.get();
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
        return parentState;
    }
}

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

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class IteratorSpellExecutor extends DefaultSpellExecutor {
    public static final StructEndec<IteratorSpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("executable", executor -> executor.executable),
            Fragment.ENDEC.listOf().fieldOf("elements", executor -> executor.elements),
            ListFragment.ENDEC.fieldOf("list", executor -> executor.list),
            Fragment.ENDEC.listOf().fieldOf("inputs", executor -> executor.inputs),
            ExecutionState.ENDEC.fieldOf("state", executor -> executor.state),
            SpellExecutor.ENDEC.optionalOf().optionalFieldOf("child", executor -> executor.child, Optional.empty()),
            IteratorSpellExecutor::new
    ));

    protected final SpellPart executable;
    protected final ListFragment list;
    protected final Stack<Fragment> elements = new Stack<>();

    protected IteratorSpellExecutor(SpellPart executable, List<Fragment> elements, ListFragment list, List<Fragment> inputs, ExecutionState state, Optional<SpellExecutor> child) {
        super(List.of(), inputs, List.of(), state, child, Optional.empty());
        this.executable = executable;
        this.list = list;
        this.elements.addAll(elements.reversed());
    }

    public IteratorSpellExecutor(SpellContext ctx, SpellPart executable, ListFragment list) {
        super(new SpellPart(), List.of());
        this.state = ctx.executionState().recurseOrThrow(List.of());
        this.executable = executable;
        this.list = list;
        this.elements.addAll(list.fragments().reversed());
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ITERATOR;
    }

    @Override
    protected Optional<Fragment> run(SpellContext ctx, ExecutionCounter executions) throws BlunderException {
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

            child = Optional.of(new DefaultSpellExecutor(executable, ctx.executionState().recurseOrThrow(List.of(elements.pop(), new NumberFragment(list.fragments().size() - elements.size() - 1), list))));
            var result = runChild(ctx, executions);

            if (result.isEmpty()) {
                return result;
            }

            executions.increment();
            lastRunExecutions = executions.getExecutions();
        }

        return Optional.of(new ListFragment(inputs));
    }
}

package dev.enjarai.trickster.spell.execution.executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class IteratorSpellExecutor extends SpellExecutor {
    public static final MapCodec<IteratorSpellExecutor> CODEC = MapCodec.recursive("iterator_spell_executor", self -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellPart.CODEC.fieldOf("executable").forGetter(executor -> executor.executable),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("elements").forGetter(executor -> executor.elements),
            ListFragment.CODEC.codec().fieldOf("list").forGetter(executor -> executor.list),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(executor -> executor.inputs),
            ExecutionState.CODEC.fieldOf("state").forGetter(executor -> executor.state),
            SpellExecutor.CODEC.get().codec().optionalFieldOf("child").forGetter(executor -> executor.child)
    ).apply(instance, IteratorSpellExecutor::new)));

    protected final SpellPart executable;
    protected final ListFragment list;
    protected final Stack<Fragment> elements = new Stack<>();

    protected IteratorSpellExecutor(SpellPart executable, List<Fragment> elements, ListFragment list, List<Fragment> inputs, ExecutionState state, Optional<SpellExecutor> child) {
        super(List.of(), inputs, List.of(), state, child, Optional.empty());
        this.executable = executable;
        this.list = list;
        this.elements.addAll(elements);
    }

    public IteratorSpellExecutor(SpellPart executable, ListFragment list) {
        super(new SpellPart(), List.of());
        this.executable = executable;
        this.list = list;
        this.elements.addAll(list.fragments());
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ITERATOR;
    }

    @Override
    protected Optional<Fragment> run(SpellContext ctx, int executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent())
        {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        int size = elements.size();

        for (int i = 0; i < size; i++) {
            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                return Optional.empty();
            }

            child = Optional.of(new SpellExecutor(executable, List.of(elements.pop(), new NumberFragment(list.fragments().size() - elements.size() - 1), list)));
            var result = runChild(ctx, executions);

            if (result.isEmpty()) {
                return result;
            }

            executions++;
            lastRunExecutions = executions;
        }

        return Optional.of(new ListFragment(inputs));
    }
}

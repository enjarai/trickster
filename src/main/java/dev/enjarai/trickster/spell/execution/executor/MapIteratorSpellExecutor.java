package dev.enjarai.trickster.spell.execution.executor;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class MapIteratorSpellExecutor extends DefaultSpellExecutor {
    public static final StructEndec<MapIteratorSpellExecutor> ENDEC = EndecTomfoolery.lazy(() -> StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("executable", executor -> executor.executable),
            StructEndecBuilder.of(Fragment.ENDEC.fieldOf("key", Map.Entry::getKey), Fragment.ENDEC.fieldOf("value", Map.Entry::getValue), Map::entry)
                    .listOf().fieldOf("elements", executor -> executor.elements),
            MapFragment.ENDEC.fieldOf("macros", executor -> executor.map),
            Fragment.ENDEC.listOf().fieldOf("inputs", executor -> executor.inputs),
            ExecutionState.ENDEC.fieldOf("state", executor -> executor.state),
            SpellExecutor.ENDEC.optionalOf().optionalFieldOf("child", executor -> executor.child, Optional.empty()),
            MapIteratorSpellExecutor::new
    ));

    protected final SpellPart executable;
    protected final MapFragment map;
    protected final Stack<Map.Entry<Fragment, Fragment>> elements = new Stack<>();

    protected MapIteratorSpellExecutor(SpellPart executable, List<Map.Entry<Fragment, Fragment>> elements, MapFragment map, List<Fragment> inputs, ExecutionState state, Optional<SpellExecutor> child) {
        super(List.of(), inputs, List.of(), state, child, Optional.empty());
        this.executable = executable;
        this.map = map;
        map.map().iterator().forEachRemaining(this.elements::add);
    }

    public MapIteratorSpellExecutor(SpellContext ctx, SpellPart executable, MapFragment map) {
        super(new SpellPart(), List.of());
        this.state = ctx.executionState().recurseOrThrow(List.of());
        this.executable = executable;
        this.map = map;
        map.map().iterator().forEachRemaining(this.elements::add);
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ITERATOR;
    }

    @Override
    protected Optional<Fragment> run(SpellContext ctx, int executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent()) {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        int size = elements.size();

        for (int i = 0; i < size; i++) {
            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                return Optional.empty();
            }

            child = Optional.of(new DefaultSpellExecutor(executable, ctx.executionState().recurseOrThrow(List.of(
                                    elements.peek().getKey(),
                                    elements.pop().getValue(),
                                    new NumberFragment(map.map().size() - elements.size() - 1), map
                    ))));
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

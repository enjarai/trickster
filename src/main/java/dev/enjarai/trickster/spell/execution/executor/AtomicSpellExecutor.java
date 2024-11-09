package dev.enjarai.trickster.spell.execution.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.EnterScopeInstruction;
import dev.enjarai.trickster.spell.ExitScopeInstruction;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellInstruction;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.AtomicChunkTooLargeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IllegalOperationInAtomicChunkBlunder;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.util.SpellUtils;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class AtomicSpellExecutor implements SpellExecutor {
    public static final StructEndec<AtomicSpellExecutor> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("root", AtomicSpellExecutor::spell),
            SpellInstruction.STACK_ENDEC.fieldOf("instructions", e -> e.instructions),
            Fragment.ENDEC.listOf().fieldOf("inputs", e -> e.inputs),
            Endec.INT.listOf().fieldOf("scope", e -> e.scope),
            ExecutionState.ENDEC.fieldOf("state", e -> e.state),
            Endec.INT.fieldOf("required_executions", e -> e.requiredExecutions),
            AtomicSpellExecutor::new
    );

    private final SpellPart root;
    private final Stack<SpellInstruction> instructions;
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();
    private final ExecutionState state;
    private final int requiredExecutions;
    private int lastRunExecutions;

    private AtomicSpellExecutor(SpellPart root, Stack<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionState state, int requiredExecutions) {
        this.root = root;
        this.instructions = instructions;
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.requiredExecutions = requiredExecutions;
    }

    private AtomicSpellExecutor(Trick trickSource, SpellPart root, Stack<SpellInstruction> instructions, ExecutionState state) throws BlunderException {
        this(root, instructions, List.of(), List.of(), state, calculateExecutionCost(trickSource, instructions));
    }
    
    public AtomicSpellExecutor(Trick trickSource, SpellPart root, ExecutionState state) throws BlunderException {
        this(trickSource, root, SpellUtils.flattenNode(root), state);
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ATOMIC;
    }

    @Override
    public SpellPart spell() {
        return root;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        return run(new SpellContext(state, source, data));
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx) throws BlunderException {
        lastRunExecutions = 0;

        if (ctx.data().getExecutions() > requiredExecutions) {
            return Optional.empty();
        }

        while (true) {
            if (state.isDelayed()) {
                throw new IllegalOperationInAtomicChunkBlunder();
            }

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                if (!scope.isEmpty())
                    state.pushStackTrace(scope.peek());

                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();

                if (scope.isEmpty())
                    return Optional.of(inputs.pop());
                else
                    state.popStackTrace();

                scope.push(scope.pop() + 1);
            } else {
                List<Fragment> args;
                {
                    var _args = new ArrayList<Fragment>();
                    for (int i = scope.peek(); i > 0; i--)
                        _args.add(inputs.pop());
                    args = _args.reversed();
                }

                if (inst.forks(ctx, args)) {
                    throw new IllegalOperationInAtomicChunkBlunder();
                } else {
                    inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args));
                }

                ctx.data().incrementExecutions();
                lastRunExecutions = ctx.data().getExecutions();
            }
        }
    }

    @Override
    public int getLastRunExecutions() {
        return lastRunExecutions;
    }

    @Override
    public ExecutionState getCurrentState() {
        return state;
    }

    private static int calculateExecutionCost(Trick trickSource, Stack<SpellInstruction> instructions) throws BlunderException {
        int cost = 0;
        
        for (var inst : instructions) {
            if (inst instanceof EnterScopeInstruction || inst instanceof ExitScopeInstruction)
                continue;

            cost++;
        }

        if (cost > Trickster.CONFIG.maxExecutionsPerSpellPerTick()) //TODO: account for cores having different limits
            throw new AtomicChunkTooLargeBlunder(trickSource);

        return cost;
    }
}

package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class SpellExecutor {
    private final Stack<SpellInstruction> instructions = new Stack<>();
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();
    private ExecutionState state;
    private Optional<SpellExecutor> child = Optional.empty();

    public static final Codec<SpellExecutor> CODEC = Codec.recursive("spell_queue", self -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(SerializedSpellInstruction.CODEC).fieldOf("instructions").forGetter(spellQueue -> spellQueue.instructions.stream().map(SpellInstruction::asSerialized).collect(Collectors.toList())),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(spellQueue -> spellQueue.inputs),
            Codec.list(Codec.INT).fieldOf("scope").forGetter(spellQueue -> spellQueue.scope),
            ExecutionState.CODEC.fieldOf("execCtx").forGetter(spellQueue -> spellQueue.state),
            self.optionalFieldOf("child").forGetter(spellQueue -> spellQueue.child)
    ).apply(instance, (instructions, inputs, scope, execCtx, child) -> {
        List<SpellInstruction> serializedInstructions = instructions.stream().map(SerializedSpellInstruction::toDeserialized).collect(Collectors.toList());
        return new SpellExecutor(serializedInstructions, inputs, scope, execCtx, child);
    })));

    private SpellExecutor(List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionState state, Optional<SpellExecutor> child) {
        this.instructions.addAll(instructions);
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.child = child;
    }

    public SpellExecutor(SpellPart root, List<Fragment> arguments) {
        this.state = new ExecutionState(arguments);
        flattenNode(root);
    }

    public SpellExecutor(SpellPart root, ExecutionState executionState) {
        this.state = executionState;
        flattenNode(root);
    }

    private void flattenNode(SpellPart node) {
        instructions.push(new ExitScopeInstruction());
        instructions.push(node.glyph);

        for (var subNode : node.subParts.reversed()) {
            flattenNode(subNode);
        }

        instructions.push(new EnterScopeInstruction());
    }

    /**
     * @return whether the spell has completed or not.
     * @throws BlunderException
     */
    public boolean run(SpellSource source) throws BlunderException {
        return run(new SpellContext(source, state), 0).isPresent();
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    private Optional<Fragment> run(SpellContext context, int executions) throws BlunderException {
        {
            var result = runChild(context, executions);

            if (result.isEmpty())
                return result;
        }

        while (true) {
            if (instructions.isEmpty()) {
                return Optional.of(inputs.pop());
            }

            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                return Optional.empty();
            }

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();
                scope.push(scope.pop() + 1);
            } else {
                List<Fragment> args;
                {
                    var _args = new ArrayList<Fragment>();
                    for (int i = scope.peek(); i > 0; i--)
                        _args.add(inputs.pop());
                    args = _args.reversed();
                }

                if (inst.forks()) {
                    var child = makeExecutorQueue(context, inst, args);

                    if (instructions.size() == 1) {
                        instructions.clear();
                        inputs.clear();
                        scope.clear();

                        instructions.addAll(child.instructions);
                        state = child.state;
                        state.decrementRecursion();
                        return run(context, executions);
                    } else {
                        this.child = Optional.of(child);
                        var result = runChild(context, executions);

                        if (result.isEmpty()) {
                            return result;
                        }
                    }
                } else {
                    // TODO pass whole context into trick, not only source
                    inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(context.source(), args));
                }

                executions++;
            }
        }
    }

    private Optional<Fragment> runChild(SpellContext context, int executions) {
        var result = child.flatMap(c -> c.run(context, executions));

        if (result.isPresent()) {
            inputs.push(result.get());
            child = Optional.empty();
        }

        return result;
    }

    private static SpellExecutor makeExecutorQueue(SpellContext context, SpellInstruction inst, List<Fragment> args) throws BlunderException {
        return inst.makeFork(context, args);
    }
}

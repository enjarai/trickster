package dev.enjarai.trickster.spell.execution.executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class DefaultSpellExecutor implements SpellExecutor {
    public static final MapCodec<DefaultSpellExecutor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(SerializedSpellInstruction.CODEC).fieldOf("instructions").forGetter(executor -> executor.instructions.stream().map(SpellInstruction::asSerialized).collect(Collectors.toList())),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(executor -> executor.inputs),
            Codec.list(Codec.INT).fieldOf("scope").forGetter(executor -> executor.scope),
            ExecutionState.CODEC.fieldOf("state").forGetter(executor -> executor.state),
            SpellExecutor.CODEC.get().codec().optionalFieldOf("child").forGetter(executor -> executor.child),
            Fragment.CODEC.get().codec().optionalFieldOf("override_return_value").forGetter(executor -> executor.overrideReturnValue)
    ).apply(instance, (instructions, inputs, scope, state, child, overrideReturnValue) -> {
        List<SpellInstruction> serializedInstructions = instructions.stream().map(SerializedSpellInstruction::toDeserialized).collect(Collectors.toList());
        return new DefaultSpellExecutor(serializedInstructions, inputs, scope, state, child, overrideReturnValue);
    }));

    protected final Stack<SpellInstruction> instructions = new Stack<>();
    protected final Stack<Fragment> inputs = new Stack<>();
    protected final Stack<Integer> scope = new Stack<>();
    protected ExecutionState state;
    protected Optional<SpellExecutor> child = Optional.empty();
    protected Optional<Fragment> overrideReturnValue = Optional.empty();
    protected int lastRunExecutions;

    protected DefaultSpellExecutor(List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionState state, Optional<SpellExecutor> child, Optional<Fragment> overrideReturnValue) {
        this.instructions.addAll(instructions);
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.child = child;
        this.overrideReturnValue = overrideReturnValue;
    }

    public DefaultSpellExecutor(SpellPart root, List<Fragment> arguments) {
        this.state = new ExecutionState(arguments);
        flattenNode(root);
    }

    public DefaultSpellExecutor(SpellPart root, ExecutionState executionState) {
        this.state = executionState;
        flattenNode(root);
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.DEFAULT;
    }

    // made non-recursive by @ArkoSammy12
    protected void flattenNode(SpellPart head) {
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
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    @Override
    public Optional<Fragment> run(SpellSource source, int executions) throws BlunderException {
        return run(new SpellContext(source, state), executions);
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    protected Optional<Fragment> run(SpellContext ctx, int executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent())
        {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        while (true) {
            if (state.isDelayed()) {
                state.decrementDelay();
                return Optional.empty();
            }

            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                return Optional.empty();
            }

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                if (!scope.isEmpty())
                    state.pushStackTrace(scope.peek());

                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();

                if (scope.isEmpty())
                    return overrideReturnValue.or(() -> Optional.of(inputs.pop()));
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
                    var child = makeExecutor(ctx, inst, args);
                    var isTail = true;
                    Fragment returnValue = null;

                    // This may be a bit jank, but it should™ usually fail fast and not cause issues
                    if (instructions.size() > 1) {
                        for (var instruction : instructions) {
                            if (instruction instanceof ExitScopeInstruction) {
                                continue;
                            } else if (instruction instanceof PatternGlyph patternGlyph && patternGlyph.pattern().isEmpty()) {
                                returnValue = VoidFragment.INSTANCE;
                                continue;
                            }

                            isTail = false;
                            break;
                        }
                    }

                    isTail = isTail && type().equals(child.type());

                    if (isTail && child instanceof DefaultSpellExecutor castChild) {
                        instructions.clear();
                        inputs.clear();
                        scope.clear();

                        // We need to be able to do this to deal with subcircles return values being gobbled up.
                        // Hopefully in the future, we can also adjust this to work for literals.
                        if (overrideReturnValue.isEmpty()) {
                            overrideReturnValue = Optional.ofNullable(returnValue);
                        }

                        instructions.addAll(castChild.instructions);
                        state = castChild.state;
                        // The new state will already have incremented recursion count, but since this isn't
                        // *technically* a recursion, we can just decrement it again.
                        state.decrementRecursions();
                        ctx = new SpellContext(ctx.source(), state);
                    } else {
                        this.child = Optional.of(child);
                        var result = runChild(ctx, executions);

                        if (result.isEmpty()) {
                            return result;
                        }
                    }
                } else {
                    inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args));
                }

                executions++;
                lastRunExecutions = executions;
            }
        }
    }

    protected Optional<Fragment> runChild(SpellContext ctx, int executions) {
        var result = child.flatMap(c -> c.run(ctx.source(), executions));

        if (result.isPresent()) {
            inputs.push(result.get());
            child = Optional.empty();
        }

        return result;
    }

    protected static SpellExecutor makeExecutor(SpellContext context, SpellInstruction inst, List<Fragment> args) throws BlunderException {
        return inst.makeFork(context, args);
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

package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class DefaultSpellExecutor implements SpellExecutor {
    public static final StructEndec<DefaultSpellExecutor> ENDEC = StructEndecBuilder.of(
            SerializedSpellInstruction.ENDEC.listOf().xmap((l) -> {
                var s = new Stack<SpellInstruction>();
                s.addAll(l.stream().map(SerializedSpellInstruction::toDeserialized).toList());
                return s;
            }, (s) -> s.stream().map(SpellInstruction::asSerialized).toList()).fieldOf("instructions", e -> e.instructions),
            Fragment.ENDEC.listOf().fieldOf("inputs", e -> e.inputs),
            Endec.INT.listOf().fieldOf("scope", e -> e.scope),
            ExecutionState.ENDEC.fieldOf("state", e -> e.state),
            EndecTomfoolery.safeOptionalOf(SpellExecutor.ENDEC).optionalFieldOf("child", e -> e.child, Optional.empty()),
            EndecTomfoolery.safeOptionalOf(Fragment.ENDEC).optionalFieldOf("override_return_value", e -> e.overrideReturnValue, Optional.empty()),
            DefaultSpellExecutor::new
    );

    protected final Stack<SpellInstruction> instructions;
    protected final Stack<Fragment> inputs = new Stack<>();
    protected final Stack<Integer> scope = new Stack<>();
    protected ExecutionState state;
    protected Optional<SpellExecutor> child = Optional.empty();
    protected Optional<Fragment> overrideReturnValue = Optional.empty();
    protected int lastRunExecutions;

    protected DefaultSpellExecutor(Stack<SpellInstruction> instructions,
                                   List<Fragment> inputs,
                                   List<Integer> scope,
                                   ExecutionState state,
                                   Optional<SpellExecutor> child,
                                   Optional<Fragment> overrideReturnValue) {
        this.instructions = instructions;
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.child = child;
        this.overrideReturnValue = overrideReturnValue;
    }

    public DefaultSpellExecutor(SpellPart root, ExecutionState executionState) {
        this.state = executionState;
        this.instructions = flattenNode(root);
    }

    public DefaultSpellExecutor(SpellPart root, List<Fragment> arguments) {
        this(root, new ExecutionState(arguments));
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.DEFAULT;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, ExecutionCounter executions) throws BlunderException {
        return run(new SpellContext(source, state), executions);
    }

    public Optional<Fragment> run(SpellContext ctx, ExecutionCounter executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent()) {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        while (true) {
            if (state.isDelayed()) {
                state.decrementDelay();
                return Optional.empty();
            }

            if (executions.isLimitReached()) {
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

                    isTail = isTail && type().equals(child.type()); //TODO: is this extra check needed?

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

                        if (result.isEmpty())
                            return result;
                    }
                } else {
                    inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args));
                }

                executions.increment();
                lastRunExecutions = executions.getExecutions();
            }
        }
    }

    protected Optional<Fragment> runChild(SpellContext ctx, ExecutionCounter executions) {
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

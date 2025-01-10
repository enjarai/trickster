package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.util.SpellUtils;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public final class DefaultSpellExecutor implements SpellExecutor {
    public static final StructEndec<DefaultSpellExecutor> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("root", e -> e.root),
            SpellInstruction.STACK_ENDEC.fieldOf("instructions", e -> e.instructions),
            Fragment.ENDEC.listOf().fieldOf("inputs", e -> e.inputs),
            Endec.INT.listOf().fieldOf("scope", e -> e.scope),
            ExecutionState.ENDEC.fieldOf("state", e -> e.state),
            EndecTomfoolery.safeOptionalOf(SpellExecutor.ENDEC).optionalFieldOf("child", e -> e.child, Optional.empty()),
            EndecTomfoolery.safeOptionalOf(Fragment.ENDEC).optionalFieldOf("override_return_value", e -> e.overrideReturnValue, Optional.empty()),
            DefaultSpellExecutor::new
    );

    private final SpellPart root;
    private final Stack<SpellInstruction> instructions;
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();
    private ExecutionState state;
    private Optional<SpellExecutor> child = Optional.empty();
    private Optional<Fragment> overrideReturnValue = Optional.empty();
    private int lastRunExecutions;

    private DefaultSpellExecutor(
            SpellPart root,
            Stack<SpellInstruction> instructions,
            List<Fragment> inputs,
            List<Integer> scope,
            ExecutionState state,
            Optional<SpellExecutor> child,
            Optional<Fragment> overrideReturnValue
    ) {
        this.root = root;
        this.instructions = instructions;
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.child = child;
        this.overrideReturnValue = overrideReturnValue;
    }

    public DefaultSpellExecutor(SpellPart root, ExecutionState executionState) {
        this.root = root;
        this.state = executionState;
        this.instructions = SpellUtils.flattenNode(root);
    }

    public DefaultSpellExecutor(SpellPart root, List<Fragment> arguments) {
        this(root, new ExecutionState(arguments));
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.DEFAULT;
    }

    @Override
    public SpellPart spell() {
        return root;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        return run(new SpellContext(state, source, data));
    }

    public Optional<Fragment> run(SpellContext ctx) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent()) {
            if (!runChild(ctx)) {
                return Optional.empty();
            }
        }

        while (true) {
            if (state.isDelayed()) {
                state.decrementDelay();
                return Optional.empty();
            }

            if (ctx.data().isExecutionLimitReached()) {
                return Optional.empty();
            }

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                if (!scope.isEmpty()) {
                    state.pushStackTrace(scope.peek());
                }

                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();

                if (scope.isEmpty()) {
                    return overrideReturnValue.or(() -> Optional.of(inputs.pop()));
                } else {
                    state.popStackTrace();
                }

                scope.push(scope.pop() + 1);
            } else {
                List<Fragment> args;
                {
                    var _args = new ArrayList<Fragment>();
                    for (int i = scope.peek(); i > 0; i--)
                        _args.add(inputs.pop());
                    args = _args.reversed();
                }

                var result = inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args);

                if (result instanceof SpellExecutor executor) {
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

                    if (isTail && executor instanceof DefaultSpellExecutor defaultExecutor) {
                        instructions.clear();
                        inputs.clear();
                        scope.clear();

                        // We need to be able to do this to deal with subcircles return values being gobbled up.
                        // Hopefully in the future, we can also adjust this to work for literals.
                        if (overrideReturnValue.isEmpty()) {
                            overrideReturnValue = Optional.ofNullable(returnValue);
                        }

                        instructions.addAll(defaultExecutor.instructions);
                        state = defaultExecutor.state;
                        // The new state will already have incremented recursion count, but since this isn't
                        // *technically* a recursion, we can just decrement it again.
                        state.decrementRecursions();
                        ctx = new SpellContext(state, ctx.source(), ctx.data());
                    } else {
                        this.child = Optional.of(executor);

                        if (!runChild(ctx)) {
                            return Optional.empty();
                        }
                    }
                } else if (result instanceof Fragment fragment) {
                    inputs.push(fragment);
                } else {
                    throw new UnsupportedOperationException();
                }

                ctx.data().incrementExecutions();
                lastRunExecutions = ctx.data().getExecutions();
            }
        }
    }

    private boolean runChild(SpellContext ctx) {
        var result = child.flatMap(c -> c.run(ctx.source(), ctx.data()));

        if (result.isPresent()) {
            inputs.push(result.get());
            child = Optional.empty();
        }

        return result.isPresent();
    }

    @Override
    public int getLastRunExecutions() {
        return child.map(SpellExecutor::getLastRunExecutions).orElse(lastRunExecutions);
    }

    @Override
    public ExecutionState getDeepestState() {
        return child.map(SpellExecutor::getDeepestState).orElse(state);
    }

    //TODO: add way to turn this and all children into a SpellPart using MiscUtils
}

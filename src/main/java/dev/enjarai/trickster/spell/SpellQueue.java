package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class SpellQueue {
    private final SpellContext context;
    private final Stack<SpellInstruction> instructions = new Stack<>();
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();
    private final ExecutionContext execCtx;
    private Optional<SpellQueue> child = Optional.empty();

    public static final Codec<SpellQueue> CODEC = Codec.recursive("spell_queue", self -> RecordCodecBuilder.create(instance -> instance.group(
            SpellContext.CODEC.get().fieldOf("context").forGetter(spellQueue -> spellQueue.context),
            Codec.list(SerializedSpellInstruction.CODEC).fieldOf("instructions").forGetter(spellQueue -> spellQueue.instructions.stream().map(SpellInstruction::asSerialized).collect(Collectors.toList())),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(spellQueue -> spellQueue.inputs),
            Codec.list(Codec.INT).fieldOf("scope").forGetter(spellQueue -> spellQueue.scope),
            ExecutionContext.CODEC.fieldOf("execCtx").forGetter(spellQueue -> spellQueue.execCtx),
            self.optionalFieldOf("child").forGetter(spellQueue -> spellQueue.child)
    ).apply(instance, (ctx, instructions, inputs, scope, execCtx, child) -> {
        List<SpellInstruction> serializedInstructions = instructions.stream().map(SerializedSpellInstruction::toDeserialized).collect(Collectors.toList());
        return new SpellQueue(ctx, serializedInstructions, inputs, scope, execCtx, child);
    })));

    private SpellQueue(SpellContext ctx, List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionContext execCtx, Optional<SpellQueue> child) {
        this.context = ctx;
        this.instructions.addAll(instructions);
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.execCtx = execCtx;
        this.child = child;
    }

    public SpellQueue(SpellContext ctx, SpellPart root) {
        this.context = ctx;
        this.execCtx = new ExecutionContext();
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
    public boolean run() throws BlunderException {
        return run(this.execCtx, 0).isPresent();
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    public Optional<Fragment> run(ExecutionContext execCtx, int executions) throws BlunderException {
        {
            var result = runChild(execCtx, executions);

            if (result.isEmpty())
                return result;
        }

        while (true) {
            if (instructions.isEmpty())
                return Optional.of(inputs.pop());

            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick())
                return Optional.empty();

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
                    var child = makeExecutorQueue(context, execCtx, inst, args);

                    if (instructions.size() == 1) {
                        instructions.clear();
                        inputs.clear();
                        scope.clear();

                        instructions.addAll(child.instructions);
                        return run(execCtx, executions);
                    } else {
                        this.child = Optional.of(child);
                        var result = runChild(execCtx, executions);

                        if (result.isEmpty())
                            return result;
                    }
                } else inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(context, args));

                executions++;
            }
        }
    }

    private Optional<Fragment> runChild(ExecutionContext execCtx, int executions) {
        var result = child.flatMap(c -> c.run(execCtx, executions));

        if (result.isPresent()) {
            inputs.push(result.get());
            execCtx.decrementRecursion();
            context.popStackTrace();
            context.popPartGlyph();
            child = Optional.empty();
        }

        return result;
    }

    private static SpellQueue makeExecutorQueue(SpellContext context, ExecutionContext execCtx, SpellInstruction inst, List<Fragment> args) throws BlunderException {
        execCtx.recurseOrThrow();

        var result = inst.makeFork(context, args);
        execCtx.decrementRecursion();

        return result;
    }
}

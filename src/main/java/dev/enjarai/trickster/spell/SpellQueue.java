package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class SpellQueue {
    private final SpellContext ctx;
    private final Stack<SpellInstruction> instructions = new Stack<>();
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();

    public static final Codec<SpellQueue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellContext.CODEC.get().fieldOf("ctx").forGetter(spellQueue -> spellQueue.ctx),
            Codec.list(SerializedSpellInstruction.CODEC).fieldOf("instructions").forGetter(spellQueue -> spellQueue.instructions.stream().map(SpellInstruction::asSerialized).collect(Collectors.toList())),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(spellQueue -> spellQueue.inputs),
            Codec.list(Codec.INT).fieldOf("scope").forGetter(spellQueue -> spellQueue.scope)
    ).apply(instance, (ctx, instructions, inputs, scope) -> {
        List<SpellInstruction> serializedInstructions = instructions.stream().map(SerializedSpellInstruction::toDeserialized).collect(Collectors.toList());
        return new SpellQueue(ctx, serializedInstructions, inputs, scope);
    }));

    private SpellQueue(SpellContext ctx, List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope) {
        this.ctx = ctx;
        this.instructions.addAll(instructions);
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
    }

    public SpellQueue(SpellContext ctx, SpellPart root) {
        this.ctx = ctx;
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
     * @return The spell result, or Optional.empty() if the spell is not done executing.
     */
    public Optional<Fragment> run() {
        while (true) {
            if (instructions.isEmpty())
                return Optional.of(inputs.pop());

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();
                scope.push(scope.pop() + 1);
            } else {
                var args = new ArrayList<Fragment>();

                for (int i = scope.peek(); i > 0; i--)
                    args.add(inputs.pop());

                inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args.reversed()));
            }
        }
    }
}

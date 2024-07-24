package dev.enjarai.trickster.spell;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

public class SpellQueue {
    private final SpellContext ctx;
    private final Stack<SpellInstruction> instructions = new Stack<>();
    private final Stack<Fragment> inputs = new Stack<>();
    private final Stack<Integer> scope = new Stack<>();

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

    /*
     * Returns the spell result, or Optional.empty() if the spell is not done executing.
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

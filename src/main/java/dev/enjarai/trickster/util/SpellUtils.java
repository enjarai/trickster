package dev.enjarai.trickster.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import dev.enjarai.trickster.spell.EnterScopeInstruction;
import dev.enjarai.trickster.spell.ExitScopeInstruction;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellInstruction;
import dev.enjarai.trickster.spell.SpellPart;

public class SpellUtils {
    @SuppressWarnings("unchecked")
    public static SpellPart decodeInstructions(
            Stack<SpellInstruction> instructions, Stack<Integer> scope, Stack<Fragment> inputs,
            Optional<Fragment> overrideReturnValue
    ) {
        var children = new Stack<SpellPart>();
        instructions = (Stack<SpellInstruction>) instructions.clone();
        scope = (Stack<Integer>) scope.clone();

        for (var input : inputs) {
            children.push(new SpellPart(input));
        }

        while (instructions.size() > 0) {
            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();

                if (!scope.isEmpty())
                    scope.push(scope.pop() + 1);
            } else {
                List<SpellPart> args;
                {
                    var _args = new ArrayList<SpellPart>();
                    for (int i = scope.peek(); i > 0; i--)
                        _args.add(children.pop());
                    args = _args.reversed();
                }

                children.push(new SpellPart((Fragment) inst, args));
            }
        }

        return overrideReturnValue.map(fragment -> new SpellPart(fragment, new ArrayList<>(children).reversed())).orElse(children.pop());
    }

    // made non-recursive by @ArkoSammy12
    public static Stack<SpellInstruction> flattenNode(SpellPart head) {
        Stack<SpellInstruction> instructions = new Stack<>();
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

        return instructions;
    }
}

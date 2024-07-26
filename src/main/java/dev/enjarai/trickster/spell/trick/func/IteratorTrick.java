package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class IteratorTrick extends Trick implements ForkingTrick {
    public IteratorTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 5, 8, 7, 4, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return null;
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var list = expectInput(fragments, FragmentType.LIST, 1);

        // TODO: this wont work with codecs, find a way around that?
        return new SpellExecutor(executable, new ExecutionState(List.of())) {
            private final Stack<Fragment> elements = new Stack<>();
            private boolean hasInit = false;

            @Override
            protected Optional<Fragment> run(SpellContext ctx, int executions) throws BlunderException {
                if (!hasInit) {
                    elements.addAll(list.fragments());
                    hasInit = true;
                }

                {
                    var result = runChild(ctx, executions);

                    if (result.isEmpty())
                        return result;
                }

                int size = elements.size();

                for (int i = 0; i < size; i++) {
                    if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                        return Optional.empty();
                    }

                    this.child = Optional.of(new SpellExecutor(executable, List.of(elements.pop())));
                    var result = runChild(ctx, executions);

                    if (result.isEmpty()) {
                        return result;
                    }
                }

                return Optional.of(new ListFragment(inputs));
            }
        };
    }
}

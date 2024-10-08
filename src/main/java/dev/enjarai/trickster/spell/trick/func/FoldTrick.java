package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FoldableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class FoldTrick extends Trick implements ForkingTrick {
    public FoldTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 5, 8, 7, 4, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return makeFork(ctx, fragments).singleTickRun(ctx);
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);
        return expectInput(fragments, FoldableFragment.class, 1).fold(ctx, executable, expectInput(fragments, 2));
    }
}

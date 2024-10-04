package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ExecuteWithinCurrentScopeTrick extends Trick implements ForkingTrick {
    public ExecuteWithinCurrentScopeTrick() {
        super(Pattern.of(0, 1, 4, 5, 8, 7, 6, 3, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return makeFork(ctx, fragments).singleTickRun(ctx);
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);
        return new DefaultSpellExecutor(executable, ctx.executionState().recurseOrThrow(ctx.executionState().getArguments()));
    }
}

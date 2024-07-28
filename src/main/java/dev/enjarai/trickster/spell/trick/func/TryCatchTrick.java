package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.TryCatchSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class TryCatchTrick extends Trick implements ForkingTrick {
    public TryCatchTrick() {
        super(Pattern.of(1, 6, 8, 1, 5, 2, 0, 3, 1, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return makeFork(ctx, fragments).singleTickRun(ctx.source());
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var try_spell = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var catch_spell = expectInput(fragments, FragmentType.SPELL_PART, 1);
        return new TryCatchSpellExecutor(ctx, try_spell, catch_spell, fragments.subList(2, fragments.size()));
    }
}

package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class MaxManaReflectionTrick extends Trick {
    public MaxManaReflectionTrick() {
        super(Pattern.of(2, 5, 7, 3, 1, 5, 4, 3, 0, 4, 2, 1, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(ctx.getManaPool().getMax(ctx.source().getWorld()));
    }
}

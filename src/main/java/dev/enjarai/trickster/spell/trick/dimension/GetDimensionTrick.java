package dev.enjarai.trickster.spell.trick.dimension;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.DimensionFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class GetDimensionTrick extends Trick {
    public GetDimensionTrick() {
        super(Pattern.of(4, 0, 1, 4, 3, 6, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return DimensionFragment.of(ctx.source().getWorld());
    }
}

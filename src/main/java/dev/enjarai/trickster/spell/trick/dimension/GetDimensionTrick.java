package dev.enjarai.trickster.spell.trick.dimension;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.DimensionFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class GetDimensionTrick extends Trick<GetDimensionTrick> {
    public GetDimensionTrick() {
        super(Pattern.of(4, 0, 1, 4, 3, 6, 5, 2, 4), Signature.of(GetDimensionTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        return DimensionFragment.of(ctx.source().getWorld());
    }
}

package dev.enjarai.trickster.spell.trick.dimension;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GetTimeTrick extends Trick<GetTimeTrick> {
    public GetTimeTrick() {
        super(Pattern.of(4, 6, 3, 0, 1, 2, 5, 8, 6), Signature.of(GetTimeTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        return new NumberFragment(ctx.source().getWorld().getTimeOfDay());
    }
}

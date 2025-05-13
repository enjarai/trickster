package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class GetLightLevelTrick extends Trick<GetLightLevelTrick> {
    public GetLightLevelTrick() {
        super(Pattern.of(8, 4, 0, 1, 2, 0, 6, 8, 2), Signature.of(FragmentType.VECTOR, GetLightLevelTrick::get));
    }

    public Fragment get(SpellContext ctx, VectorFragment pos) throws BlunderException {
        expectLoaded(ctx, pos.toBlockPos());
        return new NumberFragment(ctx.source().getWorld().getLightLevel(pos.toBlockPos()));
    }
}

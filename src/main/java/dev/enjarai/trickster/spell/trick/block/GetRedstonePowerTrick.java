package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetRedstonePowerTrick extends Trick<GetRedstonePowerTrick> {
    public GetRedstonePowerTrick() {
        super(Pattern.of(1, 4, 7, 2, 0, 7), Signature.of(FragmentType.VECTOR, GetRedstonePowerTrick::get, FragmentType.NUMBER));
    }

    public NumberFragment get(SpellContext ctx, VectorFragment pos) {
        expectLoaded(ctx, pos.toBlockPos());
        return new NumberFragment(ctx.source().getWorld().getReceivedRedstonePower(pos.toBlockPos()));
    }
}

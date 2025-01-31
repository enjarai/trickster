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
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GetRedstonePowerTrick extends Trick<GetRedstonePowerTrick> {
    public GetRedstonePowerTrick() {
        super(Pattern.of(1, 4, 7, 2, 0, 7), Signature.of(FragmentType.VECTOR, GetRedstonePowerTrick::get));
    }

    public Fragment get(SpellContext ctx, VectorFragment pos) throws BlunderException {
        expectLoaded(ctx, pos.toBlockPos());
        return new NumberFragment(ctx.source().getWorld().getReceivedRedstonePower(pos.toBlockPos()));
    }
}

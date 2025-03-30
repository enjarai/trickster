package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class CheckBlockTrick extends Trick<CheckBlockTrick> {
    public CheckBlockTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5), Signature.of(FragmentType.VECTOR, CheckBlockTrick::check));
    }

    public Fragment check(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();

        expectLoaded(ctx, blockPos);

        return new BlockTypeFragment(ctx.source().getWorld().getBlockState(blockPos).getBlock());
    }
}

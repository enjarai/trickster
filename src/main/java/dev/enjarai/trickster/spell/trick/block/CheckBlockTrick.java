package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class CheckBlockTrick extends Trick<CheckBlockTrick> {
    public CheckBlockTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5), Signature.of(FragmentType.VECTOR, CheckBlockTrick::check, FragmentType.BLOCK_TYPE));
    }

    public BlockTypeFragment check(SpellContext ctx, VectorFragment pos) {
        var blockPos = pos.toBlockPos();

        expectLoaded(ctx, blockPos);

        return new BlockTypeFragment(ctx.source().getWorld().getBlockState(blockPos).getBlock());
    }
}

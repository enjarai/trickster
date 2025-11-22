package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetBlockHardnessTrick extends Trick<GetBlockHardnessTrick> {
    public GetBlockHardnessTrick() {
        super(Pattern.of(1, 2, 8, 6, 0, 4, 2, 0, 1), Signature.of(FragmentType.VECTOR, GetBlockHardnessTrick::get, FragmentType.NUMBER));
        overload(Signature.of(FragmentType.BLOCK_TYPE, GetBlockHardnessTrick::getFromBlock, FragmentType.NUMBER));
    }

    public NumberFragment get(SpellContext ctx, VectorFragment pos) {
        var blockPos = pos.toBlockPos();
        expectLoaded(ctx, blockPos);

        var state = ctx.source().getWorld().getBlockState(blockPos);
        var hardness = state.getBlock().getHardness();

        return new NumberFragment(hardness < 0 ? Float.MAX_VALUE : hardness);

    }

    public NumberFragment getFromBlock(SpellContext ctx, BlockTypeFragment block) {
        var hardness = block.block().getHardness();
        return new NumberFragment(hardness < 0 ? Float.MAX_VALUE : hardness);
    }
}

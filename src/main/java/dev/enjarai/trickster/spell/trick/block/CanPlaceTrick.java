package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class CanPlaceTrick extends Trick<CanPlaceTrick> {
    public CanPlaceTrick() {
        super(Pattern.of(0, 2, 8, 5, 2, 4, 6, 8, 4, 0, 6, 3, 0), Signature.of(FragmentType.VECTOR, FragmentType.BLOCK_TYPE.optionalOf(), CanPlaceTrick::check, FragmentType.BOOLEAN));
    }

    public BooleanFragment check(SpellContext ctx, VectorFragment pos, Optional<BlockTypeFragment> blockType) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        expectLoaded(ctx, blockPos);

        boolean result;

        result = blockType.map(blockTypeFragment -> blockTypeFragment.block().getDefaultState().canPlaceAt(world, blockPos))
                .orElseGet(() -> world.getBlockState(blockPos).isReplaceable());

        return BooleanFragment.of(result);
    }
}

package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class CanPlaceTrick extends Trick {
    public CanPlaceTrick() {
        super(Pattern.of(0, 2, 8, 5, 2, 4, 6, 8, 4, 0, 6, 3, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockType = supposeInput(fragments, FragmentType.BLOCK_TYPE, 1);
        var blockPos = pos.toBlockPos();
        var world = ctx.getWorld();
        boolean result;

        if (blockType.isPresent())
            result = blockType.get().block().getDefaultState().canPlaceAt(world, blockPos);
        else
            result = world.getBlockState(blockPos).isAir();

        return new BooleanFragment(result);
    }
}

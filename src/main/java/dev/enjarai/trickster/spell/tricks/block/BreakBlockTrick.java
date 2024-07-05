package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class BreakBlockTrick extends Trick {
    public BreakBlockTrick() {
        super(Pattern.of(1, 5, 8, 6, 4, 1, 0, 3, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.getWorld();
        var state = world.getBlockState(blockPos);

        if (state.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos);
        }

        float hardness = state.getBlock().getHardness();

        if (hardness >= 0 && hardness < 55.5f) {
            ctx.useMana(this, hardness);
            ctx.getCaster().ifPresentOrElse(c -> world.breakBlock(blockPos, true, c), () -> world.breakBlock(blockPos, true));
        }

        return VoidFragment.INSTANCE;
    }
}

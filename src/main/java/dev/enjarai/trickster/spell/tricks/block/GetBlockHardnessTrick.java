package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class GetBlockHardnessTrick extends Trick {
    public GetBlockHardnessTrick() {
        super(Pattern.of(1, 2, 8, 6, 0, 4, 2, 0, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var state = ctx.source().getWorld().getBlockState(blockPos);
        var hardness = state.getBlock().getHardness();

        return new NumberFragment(hardness < 0 ? Float.MAX_VALUE : hardness);

    }
}

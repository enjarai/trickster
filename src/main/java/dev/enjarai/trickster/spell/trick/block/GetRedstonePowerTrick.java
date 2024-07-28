package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class GetRedstonePowerTrick extends Trick {
    public GetRedstonePowerTrick() {
        super(Pattern.of(1, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);

        return new NumberFragment(ctx.source().getWorld().getReceivedRedstonePower(pos.toBlockPos()));
    }
}

package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GetRedstonePowerTrick extends Trick {
    public GetRedstonePowerTrick() {
        super(Pattern.of(1, 4, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        expectLoaded(ctx, blockPos);

        return new NumberFragment(ctx.source().getWorld().getReceivedRedstonePower(blockPos));
    }
}

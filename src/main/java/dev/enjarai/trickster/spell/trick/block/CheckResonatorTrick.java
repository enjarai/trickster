package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.SpellControlledRedstoneBlock;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class CheckResonatorTrick extends Trick {
    public CheckResonatorTrick() {
        super(Pattern.of(7, 8, 6, 7, 2, 1, 0, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        expectLoaded(ctx, blockPos);

        if (world.getBlockState(blockPos).getBlock() instanceof SpellControlledRedstoneBlock block) {
            return new NumberFragment(block.getPower(world, blockPos));
        }

        throw new BlockInvalidBlunder(this);
    }
}

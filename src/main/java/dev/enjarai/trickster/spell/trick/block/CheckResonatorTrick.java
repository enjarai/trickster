package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.SpellControlledRedstoneBlock;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class CheckResonatorTrick extends Trick<CheckResonatorTrick> {
    public CheckResonatorTrick() {
        super(Pattern.of(7, 8, 6, 7, 2, 1, 0, 7, 4), Signature.of(FragmentType.VECTOR, CheckResonatorTrick::check));
    }

    public Fragment check(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        if (world.getBlockState(blockPos).getBlock() instanceof SpellControlledRedstoneBlock block) {
            return new NumberFragment(block.getPower(world, blockPos));
        }

        throw new BlockInvalidBlunder(this);
    }
}

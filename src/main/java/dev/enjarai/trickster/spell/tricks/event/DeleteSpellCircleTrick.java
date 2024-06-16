package dev.enjarai.trickster.spell.tricks.event;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class DeleteSpellCircleTrick extends Trick {
    public DeleteSpellCircleTrick() {
        super(Pattern.of(6, 3, 0, 1, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var position = expectInput(fragments, FragmentType.VECTOR, 0);

        var blockPos = new BlockPos((int) position.vector().x(), (int) position.vector().y(), (int) position.vector().z());

        if (ctx.getWorld().getBlockState(blockPos).isOf(ModBlocks.SPELL_CIRCLE)) {
            ctx.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
            return BooleanFragment.TRUE;
        }

        return BooleanFragment.FALSE;
    }
}

package dev.enjarai.trickster.spell.trick.event;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.block.Blocks;

import java.util.List;

public class DeleteSpellCircleTrick extends Trick {
    public DeleteSpellCircleTrick() {
        super(Pattern.of(6, 3, 0, 1, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var position = expectInput(fragments, FragmentType.VECTOR, 0);

        var blockPos = position.toBlockPos();
        expectCanBuild(ctx, blockPos);
        ctx.useMana(this, 124);

        if (ctx.source().getWorld().getBlockState(blockPos).isOf(ModBlocks.SPELL_CIRCLE)) {
            ctx.source().getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
            return BooleanFragment.TRUE;
        }

        return BooleanFragment.FALSE;
    }
}

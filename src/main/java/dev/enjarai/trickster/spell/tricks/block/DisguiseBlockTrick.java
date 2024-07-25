package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.cca.ModChunkCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.world.chunk.EmptyChunk;

import java.util.List;

public class DisguiseBlockTrick extends AbstractBlockDisguiseTrick {
    public DisguiseBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 3, 0, 1, 2, 5, 8, 7, 6, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockType = expectInput(fragments, FragmentType.BLOCK_TYPE, 1);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        if (blockType.block().getDefaultState().isAir()) {
            throw new BlockInvalidBlunder(this);
        }

        if (world.getBlockState(blockPos).isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos);
        }

        var chunk = world.getChunk(blockPos);

        if (!(chunk instanceof EmptyChunk)) {
            ctx.useMana(this, 20);

            var component = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);

            if (component.setFunnyState(blockPos, blockType.block())) {
                updateShadow(ctx, blockPos);
                return BooleanFragment.TRUE;
            }
        }

        return BooleanFragment.FALSE;
    }
}

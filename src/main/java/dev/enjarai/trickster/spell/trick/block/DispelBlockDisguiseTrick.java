package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.cca.ModChunkComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.world.chunk.EmptyChunk;

import java.util.List;

public class DispelBlockDisguiseTrick extends AbstractBlockDisguiseTrick {
    public DispelBlockDisguiseTrick() {
        super(Pattern.of(0, 4, 8, 5, 2, 4, 6, 3, 0, 1, 4, 7, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        if (world.getBlockState(blockPos).isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos);
        }

        var chunk = world.getChunk(blockPos);

        if (!(chunk instanceof EmptyChunk)) {
            ctx.useMana(this, 10);

            var component = ModChunkComponents.SHADOW_DISGUISE_MAP.get(chunk);

            if (component.clearFunnyState(blockPos)) {
                updateShadow(ctx, blockPos);
                return BooleanFragment.TRUE;
            }
        }


        return BooleanFragment.FALSE;
    }
}

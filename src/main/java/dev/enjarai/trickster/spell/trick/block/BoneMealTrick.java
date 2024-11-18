package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.Fertilizable;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class BoneMealTrick extends Trick {
    public BoneMealTrick() {
        super(Pattern.of(0, 2, 1, 0, 7, 2, 4, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        var blockState = world.getBlockState(blockPos);

        expectCanBuild(ctx, blockPos);

        if (blockState.getBlock() instanceof Fertilizable fertilizable && fertilizable.isFertilizable(world, blockPos, blockState)) {
            ctx.useMana(this, 28);

            if (fertilizable.canGrow(world, world.random, blockPos, blockState)) {
                fertilizable.grow(world, world.random, blockPos, blockState);
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 15);
            }
        } else {
            throw new BlockInvalidBlunder(this);
        }

        return pos;
    }
}

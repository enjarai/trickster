package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;

import java.util.List;

public class DrainFluidTrick extends Trick {
    public DrainFluidTrick() {
        super(Pattern.of(3, 0, 4, 2, 5, 8, 1, 6, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        var state = world.getBlockState(blockPos);

        if (state.getBlock() == Blocks.CAULDRON) {
            world.setBlockState(blockPos, Blocks.CAULDRON.getDefaultState());
        } else if (state.getBlock() instanceof FluidDrainable drainable) {
            drainable.tryDrainFluid(ctx.source().getPlayer().orElse(null), world, blockPos, state);
        } else {
            throw new BlockUnoccupiedBlunder(this, pos);
        }
        ctx.useMana(this, 15);

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}

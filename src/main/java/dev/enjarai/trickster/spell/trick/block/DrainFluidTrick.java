package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;

public class DrainFluidTrick extends Trick<DrainFluidTrick> {
    public DrainFluidTrick() {
        super(Pattern.of(3, 0, 4, 2, 5, 8, 1, 6, 3), Signature.of(FragmentType.VECTOR, DrainFluidTrick::drain, FragmentType.VECTOR));
    }

    public VectorFragment drain(SpellContext ctx, VectorFragment pos) throws BlunderException {
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

        return pos;
    }
}

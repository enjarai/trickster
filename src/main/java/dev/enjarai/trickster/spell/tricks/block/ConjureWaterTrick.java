package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;

import java.util.List;

public class ConjureWaterTrick extends Trick {
    public ConjureWaterTrick() {
        super(Pattern.of(3, 0, 4, 2, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);

        var blockPos = pos.toBlockPos();

        expectCanBuild(ctx, blockPos);
        if (!ctx.getWorld().getBlockState(blockPos).isAir()) {
            throw new BlockOccupiedBlunder(this);
        }

        ctx.getWorld().setBlockState(blockPos, Fluids.WATER.getDefaultState().getBlockState());  //.with(WaterFluid.LEVEL, 8).getBlockState()); TODO
        ctx.getWorld().updateNeighbors(blockPos, Blocks.WATER);
        var particlePos = blockPos.toCenterPos();
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}

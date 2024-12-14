package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ErodeTrick extends Trick {
    public ErodeTrick() {
        super(Pattern.of(0, 4, 6, 7, 8, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var weatheringPosFragment = expectInput(fragments, FragmentType.VECTOR, 0);
        var weatheringPos = weatheringPosFragment.toBlockPos();
        var waterPos = expectInput(fragments, FragmentType.VECTOR, 1).toBlockPos();

        expectCanBuild(ctx, weatheringPos);
        expectCanBuild(ctx, waterPos);

        ServerWorld world = ctx.source().getWorld();
        BlockState blockState = world.getBlockState(weatheringPos);
        BlockState state = world.getBlockState(waterPos);

        if (!blockState.isAir()
                && ((state.isOf(Blocks.WATER)
                        && state.get(FluidBlock.LEVEL) == 0)
                        || state.getFluidState().isOf(Fluids.WATER)
                        || state.isOf(Blocks.WATER_CAULDRON))) {
            ctx.useMana(this, 80);

            if (state.isOf(Blocks.WATER_CAULDRON)) {
                world.setBlockState(waterPos, Blocks.CAULDRON.getDefaultState());
            } else if (state.getFluidState().isOf(Fluids.WATER)) {
                if (state.getProperties().contains(Properties.WATERLOGGED)) {
                    world.setBlockState(waterPos, state.with(Properties.WATERLOGGED, false));
                } else if (!state.isOf(Blocks.WATER)) {
                    world.breakBlock(waterPos, true, ctx.source().getCaster().orElse(null));
                    world.setBlockState(waterPos, Blocks.AIR.getDefaultState());
                } else {
                    world.setBlockState(waterPos, Blocks.AIR.getDefaultState());
                }
            }

            DataLoader.getErodeLoader().convert(blockState.getBlock(), world, weatheringPos);

            for (Direction direction : Direction.values()) {
                var offsetPos = weatheringPos.offset(direction);
                if (world.getBlockState(offsetPos).isReplaceable()) {
                    world.setBlockState(offsetPos, Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, 7));
                }
            }
        } else {
            throw new BlockInvalidBlunder(this);
        }

        return weatheringPosFragment;
    }
}

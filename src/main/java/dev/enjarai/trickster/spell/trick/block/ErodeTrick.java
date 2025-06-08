package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ErodeTrick extends Trick<ErodeTrick> {
    public ErodeTrick() {
        super(Pattern.of(0, 4, 6, 7, 8, 4, 2), Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, ErodeTrick::erode, FragmentType.VECTOR));
    }

    public VectorFragment erode(SpellContext ctx, VectorFragment weatheringPosFragment, VectorFragment waterPosFragment) throws BlunderException {
        var weatheringPos = weatheringPosFragment.toBlockPos();
        var waterPos = waterPosFragment.toBlockPos();

        expectCanBuild(ctx, weatheringPos, waterPos);

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

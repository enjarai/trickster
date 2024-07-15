package dev.enjarai.trickster.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SpellResonatorBlock extends Block implements SpellControlledRedstoneBlock {
    public static final IntProperty POWER = Properties.POWER;
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape[] SHAPE = new VoxelShape[]{
            createCuboidShape(3, 2, 3, 13, 16, 13),
            createCuboidShape(3, 0, 3, 13, 14, 13),
            createCuboidShape(3, 3, 2, 13, 13, 16),
            createCuboidShape(3, 3, 0, 13, 13, 14),
            createCuboidShape(2, 3, 3, 16, 13, 13),
            createCuboidShape(0, 3, 3, 14, 13, 13)
    };

    public SpellResonatorBlock() {
        super(Blocks.REDSTONE_BLOCK.getSettings());
        setDefaultState(getStateManager().getDefaultState()
                .with(POWER, 0)
                .with(FACING, Direction.UP)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER, FACING, WATERLOGGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE[state.get(FACING).getId()];
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess worldAccess = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return this.getDefaultState().with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER).with(FACING, ctx.getSide());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean setPower(World world, BlockPos pos, int power) {
        var state = world.getBlockState(pos);
        return world.setBlockState(pos, state.with(POWER, power));
    }
}

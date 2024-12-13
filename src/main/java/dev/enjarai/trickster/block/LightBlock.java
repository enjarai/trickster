package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class LightBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape SHAPE = createCuboidShape(5, 5, 5, 11, 11, 11);

    protected LightBlock() {
        super(
                Settings.create()
                        .noCollision().luminance(b -> 15).breakInstantly()
                        .noBlockBreakParticles().sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .replaceable()
                        .pistonBehavior(PistonBehavior.DESTROY)
        );
        setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false));
    }

    // TODO decide if we can make this work?
    //    @Override
    //    protected BlockRenderType getRenderType(BlockState state) {
    //        return BlockRenderType.MODEL;
    //    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpellColoredBlockEntity coloredBlockEntity) {
            var particlePos = Vec3d.ofCenter(pos);
            var max = random.nextInt(3);
            var colors = coloredBlockEntity.getColors();
            for (int i = 0; i < max; i++) {
                world.addParticle(
                        new SpellParticleOptions(colors[random.nextInt(colors.length)]),
                        particlePos.x, particlePos.y, particlePos.z,
                        random.nextFloat() * 0.005f - 0.0025f,
                        random.nextFloat() * 0.02f + 0.01f,
                        random.nextFloat() * 0.005f - 0.0025f
                );
            }
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess worldAccess = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return this.getDefaultState().with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.LIGHT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LightBlockEntity(pos, state);
    }
}

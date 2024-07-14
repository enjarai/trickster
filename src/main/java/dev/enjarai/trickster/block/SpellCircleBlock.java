package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SpellCircleBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final VoxelShape[] SHAPES = new VoxelShape[]{
            createCuboidShape(0, 14, 0, 16, 16, 16),
            createCuboidShape(0, 0, 0, 16, 2, 16),
            createCuboidShape(0, 0, 14, 16, 16, 16),
            createCuboidShape(0, 0, 0, 16, 16, 2),
            createCuboidShape(14, 0, 0, 16, 16, 16),
            createCuboidShape(0, 0, 0, 2, 16, 16)
    };

    protected SpellCircleBlock() {
        super(AbstractBlock.Settings.create().strength(0.5f).noCollision());
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.SPELL_CIRCLE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpellCircleBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getId()];
    }

//    @Override
//    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        if (world.getBlockState(pos.offset(state.get(FACING).getOpposite())).isAir()) {
//            return Blocks.AIR.getDefaultState();
//        }
//
//        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
//    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.SPELL_CIRCLE_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

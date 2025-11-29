package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class ColorBlock extends BlockWithEntity implements Stainable {
    public static final BooleanProperty TRANSLUCENT = BooleanProperty.of("translucent");

    protected ColorBlock() {
        super(Settings.create().sounds(BlockSoundGroup.DECORATED_POT));
        setDefaultState(getStateManager().getDefaultState().with(TRANSLUCENT, false));
    }

    // TODO decide if we can make this work?
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRANSLUCENT);
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.get(TRANSLUCENT);
    }

    @Override
    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) && (state.get(TRANSLUCENT) || !stateFrom.get(TRANSLUCENT)) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.COLOR_BLOCK);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ColorBlockEntity(pos, state);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.GRAY;
    }
}

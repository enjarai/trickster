package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
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
        super(AbstractBlock.Settings.create()
                .strength(0.5f).noCollision()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK).noBlockBreakParticles());
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

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpellColoredBlockEntity coloredBlockEntity) {
            var particlePos = Vec3d.of(pos);
            var max = random.nextInt(3);
            var shape = SHAPES[state.get(FACING).getId()].getBoundingBox();
            var colors = coloredBlockEntity.getColors();
            for (int i = 0; i < max; i++) {
                world.addParticle(
                        new SpellParticleOptions(colors[random.nextInt(colors.length)]),
                        particlePos.x + shape.minX + random.nextFloat() * shape.getLengthX(),
                        particlePos.y + shape.minY + random.nextFloat() * shape.getLengthY(),
                        particlePos.z + shape.minZ + random.nextFloat() * shape.getLengthZ(),
                        random.nextFloat() * 0.005f - 0.0025f,
                        random.nextFloat() * 0.02f + 0.01f,
                        random.nextFloat() * 0.005f - 0.0025f
                );
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof SpellCircleBlockEntity blockEntity && blockEntity.lastError != null) {
            if (world.isClient()) {
                player.sendMessage(blockEntity.lastError);
            }

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.SPELL_CIRCLE_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

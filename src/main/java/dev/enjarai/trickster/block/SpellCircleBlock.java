package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;

import dev.enjarai.trickster.item.ManaCrystalItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellCoreComponent;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE));
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
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

//    @Override
//    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
//        var blockEntity = world.getBlockEntity(pos);
//        if (blockEntity instanceof SpellColoredBlockEntity coloredBlockEntity) {
//            var particlePos = Vec3d.of(pos);
//            var max = random.nextInt(3);
//            var shape = SHAPES[state.get(FACING).getId()].getBoundingBox();
//            var colors = coloredBlockEntity.getColors();
//            for (int i = 0; i < max; i++) {
//                world.addParticle(
//                        new SpellParticleOptions(colors[random.nextInt(colors.length)]),
//                        particlePos.x + shape.minX + random.nextFloat() * shape.getLengthX(),
//                        particlePos.y + shape.minY + random.nextFloat() * shape.getLengthY(),
//                        particlePos.z + shape.minZ + random.nextFloat() * shape.getLengthZ(),
//                        random.nextFloat() * 0.005f - 0.0025f,
//                        random.nextFloat() * 0.02f + 0.01f,
//                        random.nextFloat() * 0.005f - 0.0025f
//                );
//            }
//        }
//    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof SpellCircleBlockEntity blockEntity) {
            var playerStack = player.getMainHandStack();
            
            if (playerStack == ItemStack.EMPTY || playerStack.getItem() instanceof ManaCrystalItem) {
                player.equipStack(EquipmentSlot.MAINHAND, blockEntity.removeStack(0));
                blockEntity.setStack(0, playerStack);
            }

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpellCircleBlockEntity circle) {
            SpellCoreComponent.refresh(circle.getComponents(), component -> circle.setComponents(ComponentMap.builder()
                    .addAll(circle.getComponents()).add(ModComponents.SPELL_CORE, component).build()));
            circle.markDirty();
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SpellCircleBlockEntity spellCircle && !spellCircle.isEmpty()) {
                //TODO: unneeded for loop, please clean it up eventually
                // Eh, i think we can keep it just in case, lest we forgert
                for (int i = 0; i < spellCircle.size(); ++i) {
                    ItemStack itemStack = spellCircle.getStack(i);
                    if (!itemStack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    }
                }

                spellCircle.clear();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.SPELL_CIRCLE_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

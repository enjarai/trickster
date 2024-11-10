package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.SpellCoreItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModularSpellConstructBlock extends BlockWithEntity {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;

    public static final DirectionProperty FACING = Properties.FACING;

    public static final VoxelShape[] SHAPES = new VoxelShape[] {
            VoxelShapes.union(
                    createCuboidShape(0, 6, 0, 16, 8, 16),
                    createCuboidShape(1, 8, 1, 15, 14, 15),
                    createCuboidShape(0, 14, 0, 16, 16, 16)
            ),
            VoxelShapes.union(
                    createCuboidShape(0, 0, 0, 16, 2, 16),
                    createCuboidShape(1, 2, 1, 15, 8, 15),
                    createCuboidShape(0, 8, 0, 16, 10, 16)
            ),
            VoxelShapes.union(
                    createCuboidShape(0, 0, 6, 16, 16, 8),
                    createCuboidShape(1, 1, 8, 15, 15, 14),
                    createCuboidShape(0, 0, 14, 16, 16, 16)
            ),
            VoxelShapes.union(
                    createCuboidShape(0, 0, 0, 16, 16, 2),
                    createCuboidShape(1, 1, 2, 15, 15, 8),
                    createCuboidShape(0, 0, 8, 16, 16, 10)
            ),
            VoxelShapes.union(
                    createCuboidShape(6, 0, 0, 8, 16, 16),
                    createCuboidShape(8, 1, 1, 14, 15, 15),
                    createCuboidShape(14, 0, 0, 16, 16, 16)
            ),
            VoxelShapes.union(
                    createCuboidShape(0, 0, 0, 2, 16, 16),
                    createCuboidShape(2, 1, 1, 8, 15, 15),
                    createCuboidShape(8, 0, 0, 10, 16, 16)
            )
    };

    protected ModularSpellConstructBlock() {
        super(AbstractBlock.Settings.create()
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static Optional<Integer> getSlotForHitPos(BlockHitResult hit, BlockState state) {
        return getHitPos(hit, state.get(FACING)).flatMap(hitPos -> {
            int x = Math.clamp((int) (hitPos.x * GRID_WIDTH), 0, GRID_WIDTH - 1);
            int y = Math.clamp((int) (hitPos.y * GRID_HEIGHT), 0, GRID_HEIGHT - 1);
            int slot = x + y * GRID_WIDTH;
            
            if (slot % 2 != 0)
                return Optional.empty();

            return Optional.of(slot / 2);
        });
    }

    public static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction);
            Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double x = vec3d.getX();
            double y = vec3d.getY();
            double z = vec3d.getZ();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2f((float) (1.0 - x), (float) (1.0 - y)));
                case SOUTH -> Optional.of(new Vec2f((float) x, (float) (1.0 - y)));
                case WEST -> Optional.of(new Vec2f((float) z, (float) (1.0 - y)));
                case EAST -> Optional.of(new Vec2f((float) (1.0 - z), (float) (1.0 - y)));
                case DOWN -> Optional.of(new Vec2f((float) x, (float) (1.0 - z)));
                case UP -> Optional.of(new Vec2f((float) x, (float) z));
            };
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ModularSpellConstructBlockEntity blockEntity) {
            var slot = getSlotForHitPos(hit, state);
            return slot.map(s -> {
                var slotStack = blockEntity.getStack(s);

                if (slotStack.isEmpty() && (s == 2 ? stack.isIn(ModItems.MANA_CRYSTALS) : stack.getItem() instanceof SpellCoreItem)) {
                    tryAddCore(world, pos, player, blockEntity, stack, s);
                    return ItemActionResult.success(world.isClient);
                }

                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }).orElse(ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
        } else {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ModularSpellConstructBlockEntity blockEntity) {
            var slot = getSlotForHitPos(hit, state);
            return slot.map(s -> {
                var slotStack = blockEntity.getStack(s);

                if (slotStack.isEmpty())
                    return ActionResult.CONSUME;

                tryRemoveCore(world, pos, player, blockEntity, s);
                return ActionResult.success(world.isClient);
            }).orElse(ActionResult.PASS);
        } else {
            return ActionResult.PASS;
        }
    }

    private static void tryAddCore(World world, BlockPos pos, PlayerEntity player, ModularSpellConstructBlockEntity blockEntity, ItemStack stack, int slot) {
        if (!world.isClient) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            blockEntity.setStack(slot, stack.copyAndEmpty());
            world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void tryRemoveCore(World world, BlockPos pos, PlayerEntity player, ModularSpellConstructBlockEntity blockEntity, int slot) {
        if (!world.isClient) {
            ItemStack itemStack = blockEntity.removeStack(slot).copyAndEmpty();
            world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 0.8F);
            if (!player.getInventory().insertStack(itemStack)) {
                player.dropItem(itemStack, false);
            }

            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).ordinal()];
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.MODULAR_SPELL_CONSTRUCT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularSpellConstructBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ModularSpellConstructBlockEntity circleEntity && !circleEntity.isEmpty()) {
                for (int i = 0; i < circleEntity.size(); ++i) {
                    ItemStack itemStack = circleEntity.removeStack(i);
                    if (!itemStack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    }
                }

                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.isClient()) {
            return 0;
        } else {
            return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.MODULAR_SPELL_CONSTRUCT_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

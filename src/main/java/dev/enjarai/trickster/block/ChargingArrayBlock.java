package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChargingArrayBlock extends BlockWithEntity {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;

    public static final DirectionProperty FACING = Properties.FACING;
    public static final VoxelShape[] SHAPES = new VoxelShape[] {
            createCuboidShape(0, 14, 0, 16, 16, 16),
            createCuboidShape(0, 0, 0, 16, 2, 16),
            createCuboidShape(0, 0, 14, 16, 16, 16),
            createCuboidShape(0, 0, 0, 16, 16, 2),
            createCuboidShape(14, 0, 0, 16, 16, 16),
            createCuboidShape(0, 0, 0, 2, 16, 16)
    };

    protected ChargingArrayBlock() {
        super(Settings.create()
                .strength(1.5F)
                .sounds(BlockSoundGroup.STONE)
                .solid());
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.CHARGING_ARRAY);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingArrayBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getId()];
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public static Optional<Integer> getSlotForHitPos(BlockHitResult hit, BlockState state) {
        return getHitPos(hit, state.get(FACING)).flatMap(hitPos -> {
            int x = Math.clamp((int) (hitPos.x * GRID_WIDTH), 0, GRID_WIDTH - 1);
            int y = Math.clamp((int) (hitPos.y * GRID_HEIGHT), 0, GRID_HEIGHT - 1);
            int slot = x + y * GRID_WIDTH;

            return Optional.of(slot);
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
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ChargingArrayBlockEntity blockEntity) {
            return getSlotForHitPos(hit, state).map(slot -> {
                var slotStack = blockEntity.getStack(slot);

                if (slotStack.isEmpty() && stack.isIn(ModItems.MANA_KNOTS)) {
                    tryAddCore(world, pos, player, blockEntity, stack, slot);
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
        if (world.getBlockEntity(pos) instanceof ChargingArrayBlockEntity blockEntity) {
            return getSlotForHitPos(hit, state).map(slot -> {
                var slotStack = blockEntity.getStack(slot);

                if (slotStack.isEmpty())
                    return ActionResult.CONSUME;

                tryRemoveCore(world, pos, player, blockEntity, slot);

                return ActionResult.success(world.isClient);
            }).orElse(ActionResult.PASS);
        } else {
            return ActionResult.PASS;
        }
    }

    private static void tryAddCore(World world, BlockPos pos, PlayerEntity player, ChargingArrayBlockEntity blockEntity, ItemStack stack, int slot) {
        if (!world.isClient) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            blockEntity.setStack(slot, stack.copyAndEmpty());
            world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void tryRemoveCore(World world, BlockPos pos, PlayerEntity player, ChargingArrayBlockEntity blockEntity, int slot) {
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
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChargingArrayBlockEntity spellCircle && !spellCircle.isEmpty()) {
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
        return validateTicker(type, ModBlocks.CHARGING_ARRAY_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

public class ScrollShelfBlock extends BlockWithEntity {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    protected ScrollShelfBlock() {
        super(AbstractBlock.Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(1.5F)
                .sounds(BlockSoundGroup.CHISELED_BOOKSHELF)
                .burnable());
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state) {
        return getHitPos(hit, state.get(HorizontalFacingBlock.FACING)).map(hitPos -> {
            int x = Math.clamp((int) (hitPos.x * GRID_WIDTH), 0, GRID_WIDTH - 1);
            int y = Math.clamp((int) (hitPos.y * GRID_HEIGHT), 0, GRID_HEIGHT - 1);

            return OptionalInt.of(x + y * GRID_WIDTH);
        }).orElseGet(OptionalInt::empty);
    }

    public static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction);
            Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double d = vec3d.getX();
            double e = vec3d.getY();
            double f = vec3d.getZ();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2f((float) (1.0 - d), (float) e));
                case SOUTH -> Optional.of(new Vec2f((float) d, (float) e));
                case WEST -> Optional.of(new Vec2f((float) f, (float) e));
                case EAST -> Optional.of(new Vec2f((float) (1.0 - f), (float) e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ScrollShelfBlockEntity blockEntity) {
            if (!stack.isIn(ModItems.SCROLLS)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            } else {
                OptionalInt slot = getSlotForHitPos(hit, state);
                if (slot.isEmpty()) {
                    return ActionResult.CONSUME;
                } else {
                    var slotStack = blockEntity.getStack(slot.getAsInt());
                    if (!slotStack.isEmpty()) {
                        // TODO merge stacks if required?
//                        if (ItemStack.areItemsAndComponentsEqual(stack, slotStack)) {
//
//                        }
                        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                    } else {
                        tryAddBook(world, pos, player, blockEntity, stack, slot.getAsInt());
                        return ActionResult.SUCCESS;
                    }
                }
            }
        } else {
            return ActionResult.CONSUME;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ScrollShelfBlockEntity blockEntity) {
            OptionalInt slot = getSlotForHitPos(hit, state);
            if (slot.isEmpty()) {
                return ActionResult.PASS;
            } else if (blockEntity.getStack(slot.getAsInt()).isEmpty()) {
                return ActionResult.CONSUME;
            } else {
                tryRemoveBook(world, pos, player, blockEntity, slot.getAsInt());
                return ActionResult.SUCCESS;
            }
        } else {
            return ActionResult.PASS;
        }
    }

    private static void tryAddBook(World world, BlockPos pos, PlayerEntity player, ScrollShelfBlockEntity blockEntity, ItemStack stack, int slot) {
        if (!world.isClient) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            blockEntity.setStack(slot, stack.split(stack.getCount()));
            world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void tryRemoveBook(World world, BlockPos pos, PlayerEntity player, ScrollShelfBlockEntity blockEntity, int slot) {
        if (!world.isClient) {
            ItemStack itemStack = blockEntity.removeStack(slot, 1);
            world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.getInventory().insertStack(itemStack)) {
                player.dropItem(itemStack, false);
            }

            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.SCROLL_SHELF);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ScrollShelfBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ScrollShelfBlockEntity shelfEntity && !shelfEntity.isEmpty()) {
                for (int i = 0; i < shelfEntity.size(); ++i) {
                    ItemStack itemStack = shelfEntity.getStack(i);
                    if (!itemStack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    }
                }

                shelfEntity.clear();
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
}

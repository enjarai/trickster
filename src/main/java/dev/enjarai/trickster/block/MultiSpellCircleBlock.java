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
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MultiSpellCircleBlock extends BlockWithEntity {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;

    public static final DirectionProperty FACING = Properties.FACING;

    protected MultiSpellCircleBlock() {
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

            return switch (direction) { //TODO: the UP and DOWN might need tweaking once we have graphics
                case NORTH -> Optional.of(new Vec2f((float) (1.0 - x), (float) y));
                case SOUTH -> Optional.of(new Vec2f((float) x, (float) y));
                case WEST -> Optional.of(new Vec2f((float) z, (float) y));
                case EAST -> Optional.of(new Vec2f((float) (1.0 - z), (float) y));
                case DOWN -> Optional.of(new Vec2f((float) x, (float) z));
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
        if (world.getBlockEntity(pos) instanceof MultiSpellCircleBlockEntity blockEntity) {
            var slot = getSlotForHitPos(hit, state);
            return slot.map(s -> {
                var slotStack = blockEntity.getStack(s);

                if (!slotStack.isEmpty() || s == 2 ? !stack.isIn(ModItems.MANA_CRYSTALS) : !stack.isOf(ModItems.SPELL_CORE))
                    return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

                tryAddBook(world, pos, player, blockEntity, stack, s);
                return ItemActionResult.success(world.isClient);
            }).orElse(ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
        } else {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof MultiSpellCircleBlockEntity blockEntity) {
            var slot = getSlotForHitPos(hit, state);
            return slot.map(s -> {
                var slotStack = blockEntity.getStack(s);

                if (slotStack.isEmpty())
                    return ActionResult.CONSUME;

                tryRemoveBook(world, pos, player, blockEntity, s);
                return ActionResult.success(world.isClient);
            }).orElse(ActionResult.PASS);
        } else {
            return ActionResult.PASS;
        }
    }

    private static void tryAddBook(World world, BlockPos pos, PlayerEntity player, MultiSpellCircleBlockEntity blockEntity, ItemStack stack, int slot) {
        if (!world.isClient) {
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            blockEntity.setStack(slot, stack.split(stack.getCount()));
            world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void tryRemoveBook(World world, BlockPos pos, PlayerEntity player, MultiSpellCircleBlockEntity blockEntity, int slot) {
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
        return MapCodec.unit(ModBlocks.MULTI_SPELL_CIRCLE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MultiSpellCircleBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MultiSpellCircleBlockEntity circleEntity && !circleEntity.isEmpty()) {
                for (int i = 0; i < circleEntity.size(); ++i) {
                    ItemStack itemStack = circleEntity.getStack(i);
                    if (!itemStack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    }
                }

                circleEntity.clear();
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
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
        return validateTicker(type, ModBlocks.MULTI_SPELL_CIRCLE_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick());
    }
}

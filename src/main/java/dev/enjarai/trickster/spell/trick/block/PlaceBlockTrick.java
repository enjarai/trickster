package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.CannotPlaceBlockBlunder;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class PlaceBlockTrick extends Trick<PlaceBlockTrick> {
    public PlaceBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 0), Signature.of(FragmentType.VECTOR, FragmentType.SLOT, FragmentType.VECTOR.optionalOf(), FragmentType.VECTOR.optionalOf(), PlaceBlockTrick::placeSlot, FragmentType.VECTOR));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.BLOCK_TYPE, FragmentType.VECTOR.optionalOf(), FragmentType.VECTOR.optionalOf(), PlaceBlockTrick::placeType, FragmentType.VECTOR));
    }

    public VectorFragment placeSlot(SpellContext ctx, VectorFragment pos, SlotFragment slot, Optional<VectorFragment> facing, Optional<VectorFragment> side) throws BlunderException {
        expectCanBuild(ctx, pos.toBlockPos());
        var stack = ctx.getStack(this, Optional.of(slot), item -> item.getItem() instanceof BlockItem)
                .orElseThrow(() -> new MissingItemBlunder(this));
        return place(ctx, pos, stack, facing, side);
    }

    public VectorFragment placeType(SpellContext ctx, VectorFragment pos, BlockTypeFragment type, Optional<VectorFragment> facing, Optional<VectorFragment> side) throws BlunderException {
        expectCanBuild(ctx, pos.toBlockPos());
        var stack = ctx.getStack(this, Optional.empty(), item -> item.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == type.block())
                .orElseThrow(() -> new MissingItemBlunder(this));
        return place(ctx, pos, stack, facing, side);
    }

    public VectorFragment place(SpellContext ctx, VectorFragment pos, ItemStack stack, Optional<VectorFragment> facingOptional, Optional<VectorFragment> sideOptional) throws BlunderException {
        var world = ctx.source().getWorld();
        var blockPos = pos.toBlockPos();

        var facing = facingOptional.map(VectorFragment::toDirection).orElse(Direction.DOWN);
        var side = sideOptional.map(VectorFragment::toDirection).orElse(Direction.UP);

        try {
            if (!(stack.getItem() instanceof BlockItem blockItem)) throw new ItemInvalidBlunder(this);
            //            var state = blockItem.getBlock().getDefaultState();

            //            if (!world.getBlockState(blockPos).isReplaceable() || !state.canPlaceAt(world, blockPos)) {
            //                throw new CannotPlaceBlockBlunder(this, state.getBlock(), pos);
            //            }
            //
            //            if (state.contains(Properties.WATERLOGGED)) {
            //                state = state.with(Properties.WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER);
            //            }

            var placementContext = new AutomaticItemPlacementContext(
                    ctx.source().getWorld(),
                    blockPos,
                    facing,
                    stack,
                    side
            );

            var distance = ctx.source().getPos().distance(pos.vector());
            var mana = Math.max((float) distance, 8f);
            ctx.checkMana(this, mana);

            if (!blockItem.place(placementContext).isAccepted()) {
                throw new CannotPlaceBlockBlunder(this, blockItem.getBlock(), pos);
            }

            ctx.useMana(this, mana);
            //            world.setBlockState(blockPos, state);
            //
            //            BlockSoundGroup blockSoundGroup = state.getSoundGroup();
            //            world.playSound(
            //                    null,
            //                    blockPos,
            //                    blockSoundGroup.getPlaceSound(),
            //                    SoundCategory.BLOCKS,
            //                    (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
            //                    blockSoundGroup.getPitch() * 0.8F
            //            );
            //            world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);

            return pos;
        } catch (BlunderException blunder) {
            var thisPos = ctx.source().getPos();
            world.spawnEntity(new ItemEntity(world, thisPos.x, thisPos.y, thisPos.z, stack));
            throw blunder;
        }
    }
}

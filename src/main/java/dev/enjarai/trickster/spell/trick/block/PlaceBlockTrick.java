package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.CannotPlaceBlockBlunder;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlaceBlockTrick extends Trick<PlaceBlockTrick> {
    public PlaceBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 0),
                Signature.of(FragmentType.VECTOR, FragmentType.SLOT, FragmentType.VECTOR.optionalOfArg(), FragmentType.VECTOR.optionalOfArg(), PlaceBlockTrick::placeSlot, FragmentType.VECTOR));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.BLOCK_TYPE, FragmentType.VECTOR.optionalOfArg(), FragmentType.VECTOR.optionalOfArg(), PlaceBlockTrick::placeType, FragmentType.VECTOR));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.CONTAINER, FragmentType.VECTOR.optionalOfArg(), FragmentType.VECTOR.optionalOfArg(), PlaceBlockTrick::placeContainer,
                FragmentType.VECTOR));
    }

    public VectorFragment placeSlot(SpellContext ctx, VectorFragment pos, SlotFragment slot, Optional<VectorFragment> facing, Optional<VectorFragment> side) {
        var storage = slot.getStorage(this, ctx, VariantType.ITEM);
        return placeFromStorage(ctx, pos, storage, facing, side, null);
    }

    public VectorFragment placeContainer(SpellContext ctx, VectorFragment pos, ContainerFragment container, Optional<VectorFragment> facing, Optional<VectorFragment> side) {
        var storage = container.getStorage(this, ctx, VariantType.ITEM);
        return placeFromStorage(ctx, pos, storage, facing, side, null);
    }

    public VectorFragment placeType(SpellContext ctx, VectorFragment pos, BlockTypeFragment type, Optional<VectorFragment> facing, Optional<VectorFragment> side) {
        var storage = StorageSource.Caster.INSTANCE.getStorage(this, ctx, VariantType.ITEM);
        return placeFromStorage(ctx, pos, storage, facing, side, type);
    }

    public VectorFragment placeFromStorage(SpellContext ctx, VectorFragment pos, Storage<ItemVariant> storage, Optional<VectorFragment> facing, Optional<VectorFragment> side,
            @Nullable BlockTypeFragment type) {
        for (var view : storage.nonEmptyViews()) {
            if (!(view.getResource().getItem() instanceof BlockItem blockItem)) {
                continue;
            }

            if (type != null && blockItem.getBlock() != type.block()) {
                continue;
            }

            try (var trans = Transaction.openOuter()) {
                var resource = view.getResource();
                var extracted = view.extract(resource, 1, trans);

                if (extracted == 1) {
                    var result = place(ctx, pos, resource.toStack(), facing, side);
                    trans.commit();
                    return result;
                }
            }
        }

        throw new MissingItemBlunder(this);
    }

    public VectorFragment place(SpellContext ctx, VectorFragment pos, ItemStack stack, Optional<VectorFragment> facingOptional, Optional<VectorFragment> sideOptional) {
        var blockPos = pos.toBlockPos();

        expectCanBuild(ctx, blockPos);

        var facing = facingOptional.map(VectorFragment::toDirection).orElse(Direction.DOWN);
        var side = sideOptional.map(VectorFragment::toDirection).orElse(Direction.UP);

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
        ) {
            @Override
            public Direction getPlayerLookDirection() {
                return facing;
            }
        };

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
    }
}

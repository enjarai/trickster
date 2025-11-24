package dev.enjarai.trickster.spell.fragment.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import nl.enjarai.cicada.util.duck.ConvertibleVec3d;
import org.joml.Vector3dc;

import com.mojang.datafixers.util.Either;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.pond.SlotHolderDuck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.EitherEndec;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public record SlotFragment(StorageSource.Slot slot) implements Fragment {
    public static final StructEndec<SlotFragment> V1_ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("slot", f -> {
                throw new UnsupportedOperationException();
            }),
            EndecTomfoolery.safeOptionalOf(new EitherEndec<>(EndecTomfoolery.ALWAYS_READABLE_BLOCK_POS, EndecTomfoolery.UUID, true)).fieldOf("source", f -> {
                throw new UnsupportedOperationException();
            }),
            SlotFragment::new
    );
    public static final StructEndec<SlotFragment> ENDEC = EndecTomfoolery.backwardsCompat(
            StructEndecBuilder.of(
                    StorageSource.Slot.ENDEC.fieldOf("slot", SlotFragment::slot),
                    SlotFragment::new
            ),
            V1_ENDEC
    );

    private SlotFragment(int slot, Optional<Either<BlockPos, UUID>> source) {
        this(new StorageSource.Slot(slot, source
                .map(e -> e
                        .<StorageSource>map(
                                StorageSource.Block::new,
                                StorageSource.Entity::new
                        )
                )
                .orElse(StorageSource.Caster.INSTANCE)
        ));
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return Text.literal(slot.describe());
    }

    @Override
    public int getWeight() {
        return 64;
    }

    public static List<SlotFragment> getSlots(Trick<?> trick, SpellContext ctx, StorageSource source, VariantType<?> type) {
        var storage = source.getSlottedStorage(trick, ctx, type);
        var amount = storage.getSlotCount();

        var slots = new ArrayList<SlotFragment>();
        for (int i = 0; i < amount; i++) {
            slots.add(new SlotFragment(new StorageSource.Slot(i, source)));
        }

        return slots;
    }

    public boolean applyModifier(Trick<?> trick, SpellContext ctx, Function<ItemStack, ItemStack> modifier) {
        var slot = slot().getSelfSlot(trick, ctx, VariantType.ITEM);
        var resource = slot.getResource();
        if (resource.isBlank()) {
            return false;
        }

        try (var trans = Transaction.openOuter()) {
            var total = slot.getAmount();
            var taken = slot.extract(resource, Long.MAX_VALUE, trans);

            if (total != taken) {
                return false;
            }

            var tempStack = resource.toStack(1);
            tempStack = modifier.apply(tempStack);

            var newResource = ItemVariant.of(tempStack);
            var given = slot.insert(newResource, taken, trans);

            if (given != taken) {
                return false;
            }
            trans.commit();
        }

        return true;
    }

    public void swapWith(Trick<?> trickSource, SpellContext ctx, SlotFragment other) throws BlunderException {


        var otherInv = other.getInventory(trickSource, ctx);
        var inv = getInventory(trickSource, ctx);

        if (equals(other)) {
            var stack = inv.trickster$slot_holder$takeFromSlot(slot, getStack(trickSource, ctx).getCount());
            inv.trickster$slot_holder$setStack(slot, stack);
        } else {
            var otherStack = other.getStack(trickSource, ctx);
            var stack = getStack(trickSource, ctx);

            var movedOtherStack = other.move(trickSource, ctx, otherStack.getCount(), getSourceOrCasterPos(trickSource, ctx));
            ItemStack movedStack;

            try {
                movedStack = move(trickSource, ctx, stack.getCount(), other.getSourceOrCasterPos(trickSource, ctx));
            } catch (Exception e) {
                ctx.source().offerOrDropItem(movedOtherStack);
                throw e;
            }

            try {
                if (!inv.trickster$slot_holder$setStack(slot, movedOtherStack))
                    throw new ItemInvalidBlunder(trickSource);
            } catch (Exception e) {
                ctx.source().offerOrDropItem(movedOtherStack);
                ctx.source().offerOrDropItem(movedStack);
                throw e;
            }

            try {
                if (!otherInv.trickster$slot_holder$setStack(other.slot(), movedStack))
                    throw new ItemInvalidBlunder(trickSource);
            } catch (UnsupportedOperationException e) {
                throw new ItemInvalidBlunder(trickSource);
            } catch (Exception e) {
                ctx.source().offerOrDropItem(movedStack);
                throw e;
            }
        }
    }

    public <T> long moveInto(Trick<?> trick, SpellContext ctx, SlotFragment to, long maxAmount, VariantType<T> type) throws BlunderException {

    }

    /**
     * Instead of taking items from the slot, directly reference the stored stack to modify it. This may return an empty ItemStack if applicable.
     */
    public ItemStack reference(Trick<?> trickSource, SpellContext ctx) {
        return getStack(trickSource, ctx);
    }

    public Item getItem(Trick<?> trickSource, SpellContext ctx) throws BlunderException {
        return getStack(trickSource, ctx).getItem();
    }

    public Optional<Vector3dc> getSourcePos(Trick<?> trickSource, SpellContext ctx) {
        return source
                .map(either -> Either.unwrap(
                        either
                                .mapLeft(BlockPos::toCenterPos)
                                .mapRight(uuid -> new EntityFragment(uuid, Text.literal(""))
                                        .getEntity(ctx)
                                        .orElseThrow(() -> new UnknownEntityBlunder(trickSource))
                                        .getPos())))
                .map(ConvertibleVec3d::toVector3d);
    }

    public Vector3dc getSourceOrCasterPos(Trick<?> trickSource, SpellContext ctx) {
        return getSourcePos(trickSource, ctx).orElseGet(() -> ctx.source().getPos());
    }

    private ItemStack getStack(Trick<?> trickSource, SpellContext ctx) throws BlunderException {
        SlotHolderDuck inventory = getInventory(trickSource, ctx);

        if (slot < 0 || slot >= inventory.trickster$slot_holder$size())
            throw new NoSuchSlotBlunder(trickSource);

        return inventory.trickster$slot_holder$getStack(slot);
    }

    private ItemStack takeFromSlot(Trick<?> trickSource, SpellContext ctx, int amount) throws BlunderException {
        SlotHolderDuck inventory = getInventory(trickSource, ctx);

        if (slot < 0 || slot >= inventory.trickster$slot_holder$size())
            throw new NoSuchSlotBlunder(trickSource);

        return inventory.trickster$slot_holder$takeFromSlot(slot, amount);
    }

    private SlotHolderDuck getInventory(Trick<?> trickSource, SpellContext ctx) throws BlunderException {
        return getInventoryFromSource(trickSource, ctx, source);
    }

    private static SlotHolderDuck getInventoryFromSource(Trick<?> trickSource, SpellContext ctx, Optional<Either<BlockPos, UUID>> source) throws BlunderException {
        return source.map(s -> {
            if (s.left().isPresent()) {
                var e = ctx.source().getWorld().getBlockEntity(s.left().get());
                if (e instanceof SlotHolderDuck holder)
                    return holder;
                else if (e instanceof Inventory inv)
                    return new BridgedSlotHolder(inv);
                else throw new BlockInvalidBlunder(trickSource);
            } else {
                var e = ctx.source().getWorld().getEntity(s.right().get());
                if (e instanceof SlotHolderDuck holder)
                    return holder;
                else if (e instanceof Inventory inv)
                    return new BridgedSlotHolder(inv);
                else throw new EntityInvalidBlunder(trickSource);
            }
        }).orElseGet(
                () -> ctx.source().getInventory()
                        .map(BridgedSlotHolder::new)
                        .orElseThrow(() -> new NoInventoryBlunder(trickSource))
        );
    }

    public float getMoveCost(Trick<?> trickSource, SpellContext ctx, Vector3dc pos, long amount) throws BlunderException {
        return getSourcePos(trickSource, ctx)
                .map(sourcePos -> (float) (pos.distance(sourcePos) * amount * 0.5))
                .orElse(0f);
    }

    private record BridgedSlotHolder(Inventory inv) implements SlotHolderDuck {
        @Override
        public int trickster$slot_holder$size() {
            return inv.size();
        }

        @Override
        public ItemStack trickster$slot_holder$getStack(int slot) {
            return inv.getStack(slot);
        }

        @Override
        public boolean trickster$slot_holder$setStack(int slot, ItemStack stack) {
            if (!inv.isValid(slot, stack))
                return false;

            inv.setStack(slot, stack);
            return true;
        }

        @Override
        public ItemStack trickster$slot_holder$takeFromSlot(int slot, int amount) {
            var stack = inv.getStack(slot);
            var result = stack.copyWithCount(amount);
            stack.decrement(amount);
            return result;
        }
    }
}

package dev.enjarai.trickster.spell.fragment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

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

public record SlotFragment(int slot, Optional<Either<BlockPos, UUID>> source) implements Fragment {

    public static final StructEndec<SlotFragment> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("slot", SlotFragment::slot),
            EndecTomfoolery.safeOptionalOf(new EitherEndec<>(EndecTomfoolery.ALWAYS_READABLE_BLOCK_POS, EndecTomfoolery.UUID, true)).fieldOf("source", SlotFragment::source),
            SlotFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return Text.literal(
                "slot %d at %s".formatted(
                        slot,
                        source.map(either -> {
                            var mapped = either
                                    .mapLeft(blockPos -> "%d, %d, %d".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                                    .mapRight(UUID::toString);
                            return mapped.right().orElseGet(() -> mapped.left().get());
                        }).orElse("caster")
                )
        );
    }

    @Override
    public int getWeight() {
        return 64;
    }

    public static List<SlotFragment> getSlots(Trick<?> trick, SpellContext ctx, Optional<Either<BlockPos, UUID>> source) {
        var inventory = getInventoryFromSource(trick, ctx, source);
        return IntStream.range(0, inventory.trickster$slot_holder$size()).mapToObj(slot -> {
            return new SlotFragment(slot, source);
        }).toList();
    }

    public static NumberFragment getInventoryLength(Trick<?> trick, SpellContext ctx, Optional<Either<BlockPos, UUID>> source) {
        var inventory = getInventoryFromSource(trick, ctx, source);
        return new NumberFragment(inventory.trickster$slot_holder$size());
    }

    public void setStack(ItemStack itemStack, Trick<?> trick, SpellContext ctx) {
        var inventory = getInventory(trick, ctx);
        inventory.trickster$slot_holder$setStack(slot, itemStack);
    }

    public void writeFragment(Fragment fragment, boolean closed, Optional<Text> name, Optional<ServerPlayerEntity> player, Trick<?> trick, SpellContext ctx) throws BlunderException {
        var inventory = getInventory(trick, ctx);
        var stack = inventory.trickster$slot_holder$getStack(slot);
        var updated = FragmentComponent.write(stack, fragment, closed, player, name);

        inventory.trickster$slot_holder$setStack(slot, updated.orElseThrow(() -> new ImmutableItemBlunder(trick)));
    }

    public void resetFragment(Trick<?> trick, SpellContext ctx) throws BlunderException {
        var inventory = getInventory(trick, ctx);
        var stack = inventory.trickster$slot_holder$getStack(slot);
        var updated = FragmentComponent.reset(stack);

        inventory.trickster$slot_holder$setStack(slot, updated.orElseThrow(() -> new ImmutableItemBlunder(trick)));
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

    public int moveInto(Trick<?> trickSource, SpellContext ctx, SlotFragment to, int maxAmount) throws BlunderException {
        if (equals(to)) {
            return 0;
        }

        var toStack = to.getStack(trickSource, ctx);
        var stack = getStack(trickSource, ctx);

        if (!ItemStack.areItemsAndComponentsEqual(stack, toStack) && !toStack.isEmpty()) {
            return 0;
        }

        var amountToBeMoved = Math.min(Math.min(maxAmount, stack.getCount()),
                toStack.isEmpty() ? stack.getMaxCount() : toStack.getMaxCount() - toStack.getCount());
        if (amountToBeMoved <= 0) {
            return 0;
        }

        var movedStack = move(trickSource, ctx, amountToBeMoved, to.getSourceOrCasterPos(trickSource, ctx));

        try {
            ctx.useMana(trickSource, to.getMoveCost(trickSource, ctx, getSourceOrCasterPos(trickSource, ctx), amountToBeMoved));
        } catch (Exception e) {
            ctx.source().offerOrDropItem(movedStack);
            throw e;
        }

        if (toStack.isEmpty()) {
            to.setStack(movedStack, trickSource, ctx);
        } else {
            toStack.setCount(toStack.getCount() + amountToBeMoved);
        }

        return amountToBeMoved;
    }

    public ItemStack move(Trick<?> trickSource, SpellContext ctx) throws BlunderException {
        return move(trickSource, ctx, 1);
    }

    public ItemStack move(Trick<?> trickSource, SpellContext ctx, int amount) {
        return move(trickSource, ctx, amount, ctx.source().getPos());
    }

    public ItemStack move(Trick<?> trickSource, SpellContext ctx, int amount, Vector3dc pos) throws BlunderException {
        var stack = getStack(trickSource, ctx);

        if (stack.getCount() < amount)
            throw new MissingItemBlunder(trickSource);

        ctx.useMana(trickSource, getMoveCost(trickSource, ctx, pos, amount));
        return takeFromSlot(trickSource, ctx, amount);
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

    private float getMoveCost(Trick<?> trickSource, SpellContext ctx, Vector3dc pos, int amount) throws BlunderException {
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

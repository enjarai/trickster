package dev.enjarai.trickster.spell.fragment.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.joml.Vector3dc;

import com.mojang.datafixers.util.Either;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.EitherEndec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public record SlotFragment(StorageSource.Slot slot, VariantType<?> variantType) implements Fragment {

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
                    VariantType.ENDEC.fieldOf("variantType", SlotFragment::variantType),
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
        ), VariantType.ITEM);
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
            slots.add(new SlotFragment(new StorageSource.Slot(i, source), type));
        }

        return slots;
    }

    public void assertVariantType(Trick<?> trick, VariantType<?> variantType) {
        if (variantType != variantType()) {
            throw new InvalidSlotBlunder(trick); // TODO maybe more info here
        }
    }

    public boolean applyModifier(Trick<?> trick, SpellContext ctx, Function<ItemStack, ItemStack> modifier) {
        assertVariantType(trick, VariantType.ITEM);

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

    public <T> SingleSlotStorage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return slot().getSelfSlot(trick, ctx, expectedVariantType);
    }

    public SingleSlotStorage<?> getStorage(Trick<?> trick, SpellContext ctx) {
        return slot().getSelfSlot(trick, ctx, variantType);
    }

    public <T> T getResource(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return slot().getSelfSlot(trick, ctx, expectedVariantType).getResource();
    }

    public long getAmount(Trick<?> trick, SpellContext ctx) {
        return slot().getSelfSlot(trick, ctx, variantType).getAmount();
    }

    public Item getItem(Trick<?> trick, SpellContext ctx) throws BlunderException {
        assertVariantType(trick, VariantType.ITEM);
        return getResource(trick, ctx, VariantType.ITEM).getItem();
    }

    public Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx) {
        if (slot().source() == StorageSource.Caster.INSTANCE) {
            return Optional.empty();
        } else {
            return Optional.of(slot().source().getPosition(trick, ctx));
        }
    }

    public Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx) {
        return slot().source().getPosition(trick, ctx);
    }

    public float getMoveCost(Trick<?> trickSource, SpellContext ctx, Vector3dc pos, long amount) throws BlunderException {
        return getSourcePos(trickSource, ctx)
                .map(sourcePos -> (float) (pos.distance(sourcePos) * amount * 0.5))
                .orElse(0f);
    }

    public void incurCost(Trick<?> trick, SpellContext ctx, Vector3dc pos, long amountMoved) {
        ctx.useMana(trick, getMoveCost(trick, ctx, pos, amountMoved));
    }
}

package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoSuchSlotBlunder;
import dev.enjarai.trickster.spell.blunder.NotSlottedStorageBlunder;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public sealed interface StorageSource {
    Endec<StorageSource> ENDEC = Endec.dispatchedStruct(id -> switch (id) {
        case "caster" -> Caster.ENDEC;
        case "block" -> Block.ENDEC;
        case "entity" -> Entity.ENDEC;
        case "slot" -> Slot.ENDEC;
        default -> throw new IllegalArgumentException("No such storage source exists");
    }, StorageSource::getId, Endec.STRING);

    String getId();

    <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException;

    default <T> SlottedStorage<T> getSlottedStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
        var storage = getStorage(trick, ctx, variant);

        if (storage instanceof SlottedStorage<T> slotted) {
            return slotted;
        }

        throw new NotSlottedStorageBlunder(trick);
    }

    String describe();

    default NumberFragment getInventoryLength(Trick<?> trick, SpellContext ctx, VariantType<?> type) throws BlunderException {
        return new NumberFragment(getSlottedStorage(trick, ctx, type).getSlotCount());
    }

    record Caster() implements StorageSource {
        public static final Caster INSTANCE = new Caster();
        public static final StructEndec<Caster> ENDEC = EndecTomfoolery.unit(INSTANCE);

        @Override
        public String getId() {
            return "caster";
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            if (variant == VariantType.ITEM) {
                return ctx.source().getInventory()
                        .<Storage>map(i -> InventoryStorage.of(i, null))
                        .orElseGet(Storage::empty);
            }

            return Storage.empty();
        }

        @Override
        public String describe() {
            return "caster";
        }
    }

    record Block(BlockPos pos) implements StorageSource {
        public static final StructEndec<Block> ENDEC = StructEndecBuilder.of(
                MinecraftEndecs.BLOCK_POS.fieldOf("pos", Block::pos),
                Block::new
        );

        @Override
        public String getId() {
            return "block";
        }

        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            return null;
        }

        @Override
        public String describe() {
            return "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    record Entity(UUID uuid) implements StorageSource {
        public static final StructEndec<Entity> ENDEC = StructEndecBuilder.of(
                EndecTomfoolery.UUID.fieldOf("uuid", Entity::uuid),
                Entity::new
        );

        @Override
        public String describe() {
            return uuid.toString();
        }
    }

    record Slot(int slot, StorageSource source) implements StorageSource {
        public static final StructEndec<Slot> ENDEC = StructEndecBuilder.of(
                Endec.INT.fieldOf("slot", Slot::slot),
                StorageSource.ENDEC.fieldOf("source", Slot::source),
                Slot::new
        );

        @Override
        public String getId() {
            return "slot";
        }

        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            return null;
        }

        @Override
        public String describe() {
            return "slot %d at %s".formatted(
                    slot,
                    source.describe()
            );
        }

        public <T> SingleSlotStorage<T> getSelfSlot(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            try {
                return source.getSlottedStorage(trick, ctx, variant).getSlot(slot);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchSlotBlunder(trick);
            }
        }
    }
}

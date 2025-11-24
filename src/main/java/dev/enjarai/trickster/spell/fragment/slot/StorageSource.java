package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.pond.SlotHolderDuck;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3dc;

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

    Vector3dc getPosition(Trick<?> trick, SpellContext ctx);

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

        @Override
        public Vector3dc getPosition(Trick<?> trick, SpellContext ctx) {
            return ctx.source().getPos();
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

        @SuppressWarnings("unchecked")
        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            if (variant == VariantType.ITEM) {
                var storage = ItemStorage.SIDED.find(ctx.source().getWorld(), pos, null);

                if (storage == null) {
                    throw new NoInventoryBlunder(trick);
                }

                return (Storage<T>) storage;
            }

            return Storage.empty();
        }

        @Override
        public String describe() {
            return "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public Vector3dc getPosition(Trick<?> trick, SpellContext ctx) {
            return pos.toCenterPos().toVector3d();
        }
    }

    record Entity(UUID uuid) implements StorageSource {
        public static final StructEndec<Entity> ENDEC = StructEndecBuilder.of(
                EndecTomfoolery.UUID.fieldOf("uuid", Entity::uuid),
                Entity::new
        );

        @Override
        public String getId() {
            return "entity";
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            if (variant == VariantType.ITEM) {
                var entity = ctx.source().getWorld().getEntity(uuid);

                if (entity instanceof SlotHolderDuck holder) {
//                    return holder; TODO
                } else if (entity instanceof Inventory inv) {
                    return (Storage<T>) InventoryStorage.of(inv, null);
                } else throw new NoInventoryBlunder(trick);
            }

            return Storage.empty();
        }

        @Override
        public String describe() {
            return uuid.toString();
        }

        @Override
        public Vector3dc getPosition(Trick<?> trick, SpellContext ctx) {
            var entity = ctx.source().getWorld().getEntity(uuid);

            if (entity == null) {
                throw new EntityInvalidBlunder(trick);
            }

            return entity.getPos().toVector3d();
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

        @SuppressWarnings("unchecked")
        @Override
        public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variant) throws BlunderException {
            var slot = getSelfSlot(trick, ctx, VariantType.ITEM);

            if (variant == VariantType.ITEM) {
                var container = ItemStorage.ITEM.find(slot.getResource().toStack(), ContainerItemContext.ofSingleSlot(slot));

                if (container == null) {
                    throw new NoInventoryBlunder(trick);
                }

                return (Storage<T>) container;
            }

            return Storage.empty();
        }

        @Override
        public String describe() {
            return "slot %d at %s".formatted(
                    slot,
                    source.describe()
            );
        }

        @Override
        public Vector3dc getPosition(Trick<?> trick, SpellContext ctx) {
            return source.getPosition(trick, ctx);
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

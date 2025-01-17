package dev.enjarai.trickster.block;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ManaComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ChargingArrayBlockEntity extends BlockEntity implements Inventory {
    public int age;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public ChargingArrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHARGING_ARRAY_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, true, registryLookup);
    }

    public void tick() {
        age++;

        if (getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.get(i);

                var x = (i % 3 - 1) / 16f * 15f;
                //noinspection IntegerDivisionInFloatingPointContext
                var y = (i / 3 - 1) / 16f * 15f;

                var facing = getCachedState().get(ChargingArrayBlock.FACING);

                ManaComponent.tryRecharge(
                        serverWorld,
                        getPos()
                                .toCenterPos()
                                .add(new Vec3d(facing
                                        .getUnitVector()
                                        .mul(-0.3f, new Vector3f())
                                ))
                                .add(new Vec3d(new Vector3f(x / 3f, 0, y / 3f)
                                        .rotate(facing.getRotationQuaternion())
                                )),
                        stack
                );
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirtyAndUpdateClients();
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < inventory.size()) {
            ItemStack itemStack = inventory.get(slot);
            inventory.set(slot, ItemStack.EMPTY);
            markDirtyAndUpdateClients();

            return itemStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isIn(ModItems.MANA_KNOTS)) {
            inventory.set(slot, stack);
            markDirtyAndUpdateClients();
        } else if (stack.isEmpty()) {
            removeStack(slot, 1);
        }
    }

    public void markDirtyAndUpdateClients() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isEmpty() || stack.isIn(ModItems.MANA_KNOTS) && getStack(slot).isEmpty();
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        components.<ContainerComponent>getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.inventory);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
    }
}

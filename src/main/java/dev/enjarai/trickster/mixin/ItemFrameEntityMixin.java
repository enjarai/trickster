package dev.enjarai.trickster.mixin;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntity implements SlotHolderDuck {
    public ItemFrameEntityMixin(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getHeldItemStack();

    @Shadow
    public abstract void setHeldItemStack(ItemStack stack);

    @Shadow
    protected abstract void removeFromFrame(ItemStack stack);

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {

            @Override
            public void clear() {
                var currentStack = getHeldItemStack();
                setHeldItemStack(ItemStack.EMPTY);
                removeFromFrame(currentStack);
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return getHeldItemStack().isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                return getHeldItemStack();
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                var currentStack = getHeldItemStack();
                var newStack = currentStack.split(amount);
                setHeldItemStack(currentStack);
                if (currentStack.isEmpty()) {
                    removeFromFrame(currentStack);
                }
                return newStack;
            }

            @Override
            public ItemStack removeStack(int slot) {
                var currentStack = getHeldItemStack();
                setHeldItemStack(ItemStack.EMPTY);
                removeFromFrame(currentStack);
                return currentStack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                var currentStack = getHeldItemStack();
                setHeldItemStack(stack);
                removeFromFrame(currentStack);
            }

            @Override
            public void markDirty() {
                setHeldItemStack(getHeldItemStack());
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }
        }, null);
    }
}

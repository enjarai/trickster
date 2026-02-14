package dev.enjarai.trickster.mixin.slot;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements SlotHolderDuck {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {

            @Override
            public void clear() {
                ItemEntityMixin.this.setStack(ItemStack.EMPTY);
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return ItemEntityMixin.this.getStack().isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                return ItemEntityMixin.this.getStack();
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return ItemEntityMixin.this.getStack().split(amount);
            }

            @Override
            public ItemStack removeStack(int slot) {
                var stack = ItemEntityMixin.this.getStack();
                ItemEntityMixin.this.setStack(ItemStack.EMPTY);
                return stack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                ItemEntityMixin.this.setStack(stack);
            }

            @Override
            public void markDirty() {
                ItemEntityMixin.this.setStack(ItemEntityMixin.this.getStack());
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }
        }, null);
    }
}

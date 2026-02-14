package dev.enjarai.trickster.mixin.slot;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

// Needs different logic from AbstractHorseEntity to support body armor
@Mixin(HorseEntity.class)
public abstract class HorseEntityMixin extends AbstractHorseEntity implements SlotHolderDuck {
    protected HorseEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {
            @Override
            public void clear() {
                HorseEntityMixin.this.items.clear();
                HorseEntityMixin.this.getInventory().clear();
            }

            @Override
            public int size() {
                return HorseEntityMixin.this.getInventorySize() + 1;
            }

            @Override
            public boolean isEmpty() {
                return HorseEntityMixin.this.items.isEmpty() && HorseEntityMixin.this.getInventory().isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                if (slot == 0) {
                    return HorseEntityMixin.this.items.getStack(0);
                } else if (slot == 1) {
                    return HorseEntityMixin.this.getInventory().getStack(0);
                } else {
                    return HorseEntityMixin.this.items.getStack(slot - 1);
                }
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return getStack(slot).split(amount);
            }

            @Override
            public ItemStack removeStack(int slot) {
                var stack = getStack(slot);
                setStack(slot, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if (slot == 0) {
                    HorseEntityMixin.this.items.setStack(0, stack);
                } else if (slot == 1) {
                    HorseEntityMixin.this.getInventory().setStack(0, stack);
                } else {
                    HorseEntityMixin.this.items.setStack(slot - 1, stack);
                }
            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return HorseEntityMixin.this.getInventory().canPlayerUse(player);
            }
        }, null);
    }
}

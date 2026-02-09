package dev.enjarai.trickster.mixin.slot;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AllayEntity.class)
public abstract class AllayEntityMixin extends PathAwareEntity implements SlotHolderDuck {
    protected AllayEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Final
    @Shadow
    private SimpleInventory inventory;

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        // slot 0: held item. slot 1: inventory
        return InventoryStorage.of(new Inventory() {
            @Override
            public void clear() {
                AllayEntityMixin.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                inventory.setStack(0, ItemStack.EMPTY);
            }

            @Override
            public int size() {
                return 2;
            }

            @Override
            public boolean isEmpty() {
                return inventory.isEmpty() && AllayEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                return (slot == 0) ? AllayEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND) : inventory.getStack(0);
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
                    AllayEntityMixin.this.equipStack(EquipmentSlot.MAINHAND, stack);
                } else {
                    inventory.setStack(0, stack);
                }
            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }
        }, null);
    }
}

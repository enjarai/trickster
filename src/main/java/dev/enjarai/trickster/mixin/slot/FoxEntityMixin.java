package dev.enjarai.trickster.mixin.slot;

import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity implements SlotHolderDuck {
    protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {
            @Override
            public void clear() {
                FoxEntityMixin.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return FoxEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
            }

            @Override
            public ItemStack getStack(int slot) {
                return FoxEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return FoxEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND).split(amount);
            }

            @Override
            public ItemStack removeStack(int slot) {
                var stack = FoxEntityMixin.this.getEquippedStack(EquipmentSlot.MAINHAND);
                FoxEntityMixin.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                FoxEntityMixin.this.equipStack(EquipmentSlot.MAINHAND, stack);
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

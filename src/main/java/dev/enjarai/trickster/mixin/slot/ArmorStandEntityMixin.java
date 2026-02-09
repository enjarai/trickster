package dev.enjarai.trickster.mixin.slot;

import com.google.common.collect.Iterables;
import dev.enjarai.trickster.pond.SlotHolderDuck;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin implements SlotHolderDuck {
    @Shadow
    @Final
    private DefaultedList<ItemStack> heldItems;

    @Shadow
    @Final
    private DefaultedList<ItemStack> armorItems;

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Unique
    private static final List<EquipmentSlot> equipmentSlots = Arrays.stream(EquipmentSlot.values()).toList();

    @Override
    public Storage<ItemVariant> trickster$slot_holder$getItemStorage() {
        return InventoryStorage.of(new Inventory() {

            @Override
            public void clear() {
                equipmentSlots.forEach(equipmentSlot -> equipStack(equipmentSlot, ItemStack.EMPTY));
            }

            @Override
            public int size() {
                return 6;
            }

            @Override
            public boolean isEmpty() {
                return StreamSupport.stream(Iterables.concat(heldItems, armorItems).spliterator(), true).allMatch(ItemStack::isEmpty);
            }

            @Override
            public ItemStack getStack(int slot) {
                if (slot < 2) {
                    return heldItems.get(slot);
                } else {
                    return armorItems.get(slot - 2);
                }
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                var currentStack = getStack(slot);
                var newStack = getStack(slot).split(amount);
                equipStack(equipmentSlots.get(slot), currentStack);
                return newStack;
            }

            @Override
            public ItemStack removeStack(int slot) {
                var currentStack = getStack(slot);
                equipStack(equipmentSlots.get(slot), ItemStack.EMPTY);
                return currentStack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                equipStack(equipmentSlots.get(slot), stack);
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

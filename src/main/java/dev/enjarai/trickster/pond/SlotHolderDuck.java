package dev.enjarai.trickster.pond;

import net.minecraft.item.ItemStack;

public interface SlotHolderDuck {
    int trickster$slot_holder$size();
    ItemStack trickster$slot_holder$getStack(int slot);
    ItemStack trickster$slot_holder$takeFromSlot(int slot, int amount);
}

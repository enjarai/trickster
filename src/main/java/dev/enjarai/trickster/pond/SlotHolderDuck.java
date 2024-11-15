package dev.enjarai.trickster.pond;

import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.item.ItemStack;

public interface SlotHolderDuck {
    int trickster$slot_holder$size();
    ItemStack trickster$slot_holder$getStack(int slot);
    void trickster$slot_holder$setStack(int slot, ItemStack stack) throws BlunderException;
    ItemStack trickster$slot_holder$takeFromSlot(int slot, int amount);
}

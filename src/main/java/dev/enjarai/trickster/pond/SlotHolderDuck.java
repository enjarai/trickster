package dev.enjarai.trickster.pond;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface SlotHolderDuck {
    Storage<ItemVariant> trickster$slot_holder$getItemStorage();
}

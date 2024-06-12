package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModNetworking {
    public static final OwoNetChannel MLADY = OwoNetChannel.create(Trickster.id("mlady"));

    public static void register() {
        MLADY.registerServerbound(MladyPacket.class, (packet, access) -> {
            var player = access.player();
            var inventory = player.getInventory();

            if (packet.hold()) {
                if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT) && player.getOffHandStack().isEmpty()) {
                    var headStack = inventory.getArmorStack(EquipmentSlot.HEAD.getEntitySlotId());
                    inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), ItemStack.EMPTY);
                    inventory.offHand.set(0, headStack);
                }
            } else {
                if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                    var offHandStack = inventory.offHand.getFirst();
                    inventory.offHand.set(0, ItemStack.EMPTY);
                    inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), offHandStack);
                }
            }
        });
    }
}

package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.owo.network.ServerAccess;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public record MladyPacket(boolean hold) {
    public void handleServer(ServerAccess access) {
        var player = access.player();
        var inventory = player.getInventory();

        var hat = SlotReference.of(player, "hat", 0);
        var hatStack = hat.getStack();
        if (hold()) {
            if (hatStack != null && hatStack.isIn(ModItems.HOLDABLE_HAT) && player.getOffHandStack().isEmpty()) {
                hat.setStack(ItemStack.EMPTY);
                inventory.offHand.set(0, hatStack);
                playAnimation(player);
            } else if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT) && player.getOffHandStack().isEmpty()) {
                var headStack = inventory.getArmorStack(EquipmentSlot.HEAD.getEntitySlotId());
                inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), ItemStack.EMPTY);
                inventory.offHand.set(0, headStack);
                playAnimation(player);
            }
        } else {
            if (hatStack != null && hatStack.isEmpty() && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                var offHandStack = inventory.offHand.getFirst();
                inventory.offHand.set(0, ItemStack.EMPTY);
                hat.setStack(offHandStack);
                playAnimation(player);
            } else if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                var offHandStack = inventory.offHand.getFirst();
                inventory.offHand.set(0, ItemStack.EMPTY);
                inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), offHandStack);
                playAnimation(player);
            }
        }
    }

    public void playAnimation(ServerPlayerEntity player) {
        var packet = new MladyAnimationPacket(player.getId());
        ModNetworking.CHANNEL.serverHandle(player).send(packet);
        ModNetworking.CHANNEL.serverHandle(PlayerLookup.tracking(player)).send(packet);
    }
}

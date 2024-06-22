package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ModNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Trickster.id("main"));

    public static void register() {
        CHANNEL.registerServerbound(MladyPacket.class, (packet, access) -> {
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

        CHANNEL.registerServerbound(ScrollInGamePacket.class, (packet, access) -> {
            var player = access.player();

            if (Math.abs(packet.amount()) >= 1f) {
                ItemStack stack = null;

                if (player.isSneaking() && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)) {
                    stack = player.getMainHandStack();
                } else if (player.getOffHandStack().contains(ModComponents.SELECTED_SLOT)) {
                    stack = player.getOffHandStack();
                }

                if (stack != null) {
                    var current = stack.get(ModComponents.SELECTED_SLOT);
                    var container = stack.get(DataComponentTypes.CONTAINER);

                    if (current != null && container != null) {
                        var newSlot = Math.round(current.slot() + packet.amount());
                        int maxSlot = (int) Math.min(current.maxSlot(), container.stream().count());

                        if (maxSlot > 0) {
                            while (newSlot < 0) {
                                newSlot += maxSlot;
                            }
                            while (newSlot >= maxSlot) {
                                newSlot -= maxSlot;
                            }
                        } else {
                            newSlot = 0;
                        }

                        stack.set(ModComponents.SELECTED_SLOT,
                                new SelectedSlotComponent(newSlot, current.maxSlot()));

                        player.sendMessage(Text.of("Selected slot: " + newSlot), true);
                    }
                }
            }
        });
    }
}

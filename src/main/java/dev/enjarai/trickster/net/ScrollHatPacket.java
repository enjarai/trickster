package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public record ScrollHatPacket(float amount, boolean inGame) {
    public void handleServer(ServerAccess access) {
        var player = access.player();

        if (Math.abs(amount()) >= 1f) {
            ItemStack stack = null;

            if ((player.isSneaking() || !inGame()) && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)) {
                stack = player.getMainHandStack();
            } else if (player.getOffHandStack().contains(ModComponents.SELECTED_SLOT)) {
                stack = player.getOffHandStack();
            }

            if (stack != null) {
                var current = stack.get(ModComponents.SELECTED_SLOT);
                var container = stack.get(DataComponentTypes.CONTAINER);

                if (current != null && container != null) {
                    var newSlot = Math.round(current.slot() + amount());
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

                    var name = container.stream().skip(newSlot).findFirst().filter(s -> !s.isEmpty());
                    var message = Text.translatable("trickster.scroll_hat", newSlot);

                    if (name.isPresent()) {
                        message = message.append(" [").append(name.get().getName()).append("]");
                    }

                    player.sendMessage(message, true);
                }
            }
        }
    }
}

package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.TrickHatItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public record ScrollHatPacket(float amount, boolean inGame) {
    public void handleServer(ServerAccess access) {
        var player = access.player();

        handleCommon(player);
    }

    public void handleCommon(PlayerEntity player) {
        if (Math.abs(amount()) >= 1f) {
            ItemStack stack = null;

            if ((player.isSneaking() || !inGame()) && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)) {
                stack = player.getMainHandStack();
            } else if (player.getOffHandStack().contains(ModComponents.SELECTED_SLOT)) {
                stack = player.getOffHandStack();
            }

            if (stack != null && stack.contains(DataComponentTypes.CONTAINER)) {
                var newSlot = TrickHatItem.scrollHat(stack, amount());

                var container = stack.get(DataComponentTypes.CONTAINER);

                //noinspection DataFlowIssue
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

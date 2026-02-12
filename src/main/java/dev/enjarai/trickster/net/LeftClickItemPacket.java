package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.LeftClickItem;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public record LeftClickItemPacket(Hand hand) {
    public void handleServer(ServerAccess access) {
        PlayerEntity user = access.player();
        var stack = user.getStackInHand(hand);
        if (stack.getItem() instanceof LeftClickItem leftClickItem) {
            leftClickItem.use(user.getWorld(), user, hand, false);
        }
    }
}

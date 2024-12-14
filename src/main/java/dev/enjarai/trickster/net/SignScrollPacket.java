package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.WrittenScrollMetaComponent;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.util.Hand;

public record SignScrollPacket(Hand hand, String name) {
    public void handleServer(ServerAccess access) {
        var player = access.player();
        var stack = player.getStackInHand(hand());

        if (stack.getItem() instanceof ScrollAndQuillItem scrollAndQuillItem) {
            var component = stack.get(ModComponents.FRAGMENT);
            if (component == null) {
                return;
            }

            var newStack = scrollAndQuillItem.signedVersion.getDefaultStack();

            newStack.set(ModComponents.FRAGMENT, new FragmentComponent(component.value(), component.name(), true, component.closed()));
            newStack.set(ModComponents.WRITTEN_SCROLL_META, new WrittenScrollMetaComponent(
                    name(), player.getName().getString(), 0));
            newStack.setCount(stack.getCount());

            player.setStackInHand(hand(), newStack);
            player.swingHand(hand());
        }
    }
}

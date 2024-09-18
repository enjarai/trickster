package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.item.component.WrittenScrollMetaComponent;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.util.Hand;

import java.util.Optional;

public record SignScrollPacket(Hand hand, String name) {
    public void handleServer(ServerAccess access) {
        var player = access.player();
        var stack = player.getStackInHand(hand());

        if (stack.getItem() instanceof ScrollAndQuillItem scrollAndQuillItem) {
            var component = stack.get(ModComponents.SPELL);
            if (component == null) {
                return;
            }

//            scrollAndQuillItem.

            var newStack = ModItems.WRITTEN_SCROLL.getDefaultStack(); // TODO get the proper item

            newStack.set(ModComponents.SPELL, new SpellComponent(component.spell(), component.name(), true, component.closed()));
            newStack.set(ModComponents.WRITTEN_SCROLL_META, new WrittenScrollMetaComponent(
                    name(), player.getName().getString(), 0
            ));
            newStack.setCount(stack.getCount());

            player.setStackInHand(hand(), newStack);
            player.swingHand(hand());
        }
    }
}

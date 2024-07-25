package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.item.component.WrittenScrollMetaComponent;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.util.Hand;

public record SignScrollPacket(Hand hand, String name) {
    public void handleServer(ServerAccess access) {
        var player = access.player();
        var stack = player.getStackInHand(hand());

        if (stack.isOf(ModItems.SCROLL_AND_QUILL)) {
            var component = stack.get(ModComponents.SPELL);
            if (component == null) {
                return;
            }

            var spell = component.spell();
            var newStack = ModItems.WRITTEN_SCROLL.getDefaultStack();

            newStack.set(ModComponents.SPELL, new SpellComponent(spell, true));
            newStack.set(ModComponents.WRITTEN_SCROLL_META, new WrittenScrollMetaComponent(
                    name(), player.getName().getString(), 0
            ));
            newStack.setCount(stack.getCount());

            player.setStackInHand(hand(), newStack);
            player.swingHand(hand());
        }
    }
}

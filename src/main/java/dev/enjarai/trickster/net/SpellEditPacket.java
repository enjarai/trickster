package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public record SpellEditPacket() {
    public void handleServer(ServerAccess access) {
        var player = access.player();

        if (!player.isCreative()) return;

        var stack = player.getMainHandStack();

        if (stack.isEmpty()) return;

        var mergedMap = FragmentComponent.getUserMergedMap(player, "ring", HashMap::empty);

        player.openHandledScreen(ScrollAndQuillScreenHandler.factory(
            Text.translatable("trickster.screen.scroll_and_quill"),
            new ScrollAndQuillScreenHandler.InitialData(
                FragmentComponent.getSpellPart(stack).orElse(new SpellPart()),
                true, Hand.MAIN_HAND
            ),
            stack, player.getOffHandStack()
        ));
    }
}

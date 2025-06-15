package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import io.vavr.collection.HashMap;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public record SpellEditPacket() {
    public void handleServer(ServerAccess access) {
        var player = access.player();

        if (!player.isCreative()) return;

        var stack = player.getMainHandStack();

        if (stack.isEmpty()) return;

        var mergedMap = FragmentComponent.getUserMergedMap(player, "ring", HashMap::empty);

        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("trickster.screen.scroll_and_quill");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScrollAndQuillScreenHandler(
                        syncId, playerInventory, stack, player.getOffHandStack(), EquipmentSlot.MAINHAND,
                        mergedMap, stack.isOf(ModItems.MIRROR_OF_EVALUATION), true
                );
            }
        });
    }
}

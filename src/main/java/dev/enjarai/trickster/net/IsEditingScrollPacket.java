package dev.enjarai.trickster.net;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import io.wispforest.owo.network.ServerAccess;

public record IsEditingScrollPacket(boolean isEditing) {
    public void handleServer(ServerAccess access) {
        var player = access.player();
        player.getComponent(ModEntityCumponents.IS_EDITING_SCROLL).setEditing(isEditing());
    }
}

package dev.enjarai.trickster.net;

import dev.enjarai.trickster.cca.ModEntityComponents;
import io.wispforest.owo.network.ServerAccess;

public record IsEditingScrollPacket(boolean isEditing, boolean isOffhand) {
    public void handleServer(ServerAccess access) {
        access.player().getComponent(ModEntityComponents.IS_EDITING_SCROLL).setEditing(isEditing(), isOffhand());
    }
}

package dev.enjarai.trickster.net;

import java.util.UUID;

import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.owo.network.ServerAccess;

public record SubscribeToPoolPacket(UUID uuid) {
    public void handleServer(ServerAccess access) {
        SharedManaComponent.getInstance().subscribe(access.player(), uuid);
    }
}

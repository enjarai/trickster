package dev.enjarai.trickster.net;

import java.util.Optional;

import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.owo.network.ServerAccess;

public record SubscribeInventoryPacket() {
    public void handleServer(ServerAccess access) {
        SharedManaComponent.INSTANCE.subscribeInventory(access.player(), Optional.empty());
    }
}

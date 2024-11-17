package dev.enjarai.trickster.net;

import java.util.UUID;

import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.owo.network.ServerAccess;

public record SubscribeToPoolPacket(UUID uuid) {
    public void handleServer(ServerAccess access) {
        ModGlobalComponents.SHARED_MANA.get(access.player().getWorld().getScoreboard()).subscribe(access.player(), uuid);
    }
}

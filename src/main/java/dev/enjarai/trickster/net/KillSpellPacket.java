package dev.enjarai.trickster.net;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import io.wispforest.owo.network.ServerAccess;

public record KillSpellPacket(int index) {
    public void handleServer(ServerAccess access) {
        access.player().getComponent(ModEntityCumponents.CASTER).kill(index());
    }
}

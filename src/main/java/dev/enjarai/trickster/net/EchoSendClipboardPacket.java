package dev.enjarai.trickster.net;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.ServerAccess;

public record EchoSendClipboardPacket(UUID uuid, Fragment fragment) {
    public static final StructEndec<EchoSendClipboardPacket> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", EchoSendClipboardPacket::uuid),
            Fragment.ENDEC.fieldOf("fragment", EchoSendClipboardPacket::fragment),
            EchoSendClipboardPacket::new
    );

    public void handleServer(ServerAccess access) {
        ModGlobalComponents.MESSAGE_HANDLER.get(access.player().getWorld().getScoreboard()).send(new Key.Channel(uuid), fragment);
    }
}

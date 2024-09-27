package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import io.wispforest.owo.network.OwoNetChannel;

public class ModNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Trickster.id("main"));

    public static void register() {
        CHANNEL.registerServerbound(MladyPacket.class, MladyPacket::handleServer);
        CHANNEL.registerServerbound(ScrollHatPacket.class, ScrollHatPacket::handleServer);
        CHANNEL.registerServerbound(IsEditingScrollPacket.class, IsEditingScrollPacket::handleServer);
        CHANNEL.registerServerbound(SignScrollPacket.class, SignScrollPacket::handleServer);
        CHANNEL.registerServerbound(KillSpellPacket.class, KillSpellPacket::handleServer);
        CHANNEL.registerServerbound(ClipBoardSpellResponsePacket.class, ClipBoardSpellResponsePacket.ENDEC, ClipBoardSpellResponsePacket::handleServer);
        CHANNEL.registerServerbound(LoadExampleSpellPacket.class, LoadExampleSpellPacket.ENDEC, LoadExampleSpellPacket::handleServer);

        CHANNEL.registerClientboundDeferred(RebuildChunkPacket.class);
        CHANNEL.registerClientboundDeferred(GrabClipboardSpellPacket.class);
        CHANNEL.registerClientboundDeferred(MladyAnimationPacket.class);
    }
}

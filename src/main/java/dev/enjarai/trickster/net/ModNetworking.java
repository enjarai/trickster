package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.config.Option;

import java.util.Map;

public class ModNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Trickster.id("main"));

    public static void register() {
        CHANNEL.registerServerbound(MladyPacket.class, MladyPacket::handleServer);
        CHANNEL.registerServerbound(ScrollHatPacket.class, ScrollHatPacket::handleServer);
        CHANNEL.registerServerbound(IsEditingScrollPacket.class, IsEditingScrollPacket::handleServer);
        CHANNEL.registerServerbound(SignScrollPacket.class, SignScrollPacket::handleServer);
        CHANNEL.registerServerbound(KillSpellPacket.class, KillSpellPacket::handleServer);
        CHANNEL.registerServerbound(ClipBoardSpellResponsePacket.class, ClipBoardSpellResponsePacket.ENDEC, ClipBoardSpellResponsePacket::handleServer);
        CHANNEL.registerServerbound(EchoSendClipboardPacket.class, EchoSendClipboardPacket.ENDEC, EchoSendClipboardPacket::handleServer);
        CHANNEL.registerServerbound(LoadExampleSpellPacket.class, LoadExampleSpellPacket.ENDEC, LoadExampleSpellPacket::handleServer);
        CHANNEL.registerServerbound(SpellEditPacket.class, SpellEditPacket::handleServer);
        CHANNEL.registerServerbound(SubscribeToPoolPacket.class, SubscribeToPoolPacket::handleServer);

        CHANNEL.registerClientboundDeferred(RebuildChunkPacket.class);
        CHANNEL.registerClientboundDeferred(GrabClipboardSpellPacket.class);
        CHANNEL.registerClientboundDeferred(EchoGrabClipboardPacket.class);
        CHANNEL.registerClientboundDeferred(EchoSetClipboardPacket.class, EchoSetClipboardPacket.ENDEC);
        CHANNEL.registerClientboundDeferred(MladyAnimationPacket.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T clientOrDefault(PlayerEntity player, Option.Key key, T defaultValue) {
        if (player instanceof ServerPlayerEntity serverPlayer)
            return ConfigSynchronizer.getClientOptions(serverPlayer, Trickster.CONFIG.name()) instanceof Map<Option.Key, ?> map
                    ? (T) map.get(key)
                    : defaultValue;
        else return defaultValue;
    }
}

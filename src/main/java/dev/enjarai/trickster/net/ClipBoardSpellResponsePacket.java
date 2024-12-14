package dev.enjarai.trickster.net;

import dev.enjarai.trickster.TricksterCommand;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.ServerAccess;

public record ClipBoardSpellResponsePacket(SpellPart spell) {
    public static final StructEndec<ClipBoardSpellResponsePacket> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", ClipBoardSpellResponsePacket::spell),
            ClipBoardSpellResponsePacket::new);

    public void handleServer(ServerAccess access) {
        TricksterCommand.importCallback(access.player(), spell());
    }
}

package dev.enjarai.trickster.net;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record EchoSetClipboardPacket(Fragment fragment) {
    public static final StructEndec<EchoSetClipboardPacket> ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("fragment", EchoSetClipboardPacket::fragment),
            EchoSetClipboardPacket::new
    );
}

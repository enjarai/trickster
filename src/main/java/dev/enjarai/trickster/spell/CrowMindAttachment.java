package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.EndecTomfoolery;

public record CrowMindAttachment(Fragment fragment) {
    public static final Codec<CrowMindAttachment> CODEC = EndecTomfoolery.toCodec(Fragment.ENDEC)
            .fieldOf("fragment").xmap(CrowMindAttachment::new, CrowMindAttachment::fragment).codec();
}

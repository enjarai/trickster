package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.EndecTomfoolery;

public record CrowMind(Fragment fragment) {
    public static final Codec<CrowMind> CODEC = EndecTomfoolery.toCodec(Fragment.ENDEC)
            .fieldOf("fragment").xmap(CrowMind::new, CrowMind::fragment).codec();
}

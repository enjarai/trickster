package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import io.wispforest.owo.serialization.CodecUtils;

public record CrowMind(Fragment fragment) {
    public static final Codec<CrowMind> CODEC = CodecUtils.toCodec(Fragment.ENDEC)
            .fieldOf("fragment").xmap(CrowMind::new, CrowMind::fragment).codec();
}

package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;

public record CrowMind(Fragment fragment) {
    public static final Codec<CrowMind> CODEC = Fragment.CODEC.get().codec()
            .fieldOf("fragment").xmap(CrowMind::new, CrowMind::fragment).codec();
}

package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.spell.mana.ManaPool;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.owo.serialization.CodecUtils;

public record ManaComponent(ManaPool pool) {
    public static final Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.toCodec(ManaPool.ENDEC).fieldOf("pool").forGetter(ManaComponent::pool)
    ).apply(instance, ManaComponent::new));
}

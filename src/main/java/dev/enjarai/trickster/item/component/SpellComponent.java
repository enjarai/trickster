package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.owo.serialization.CodecUtils;

public record SpellComponent(SpellPart spell, boolean immutable, boolean closed) {
    public static final Codec<SpellComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(SpellPart.ENDEC).fieldOf("spell").forGetter(SpellComponent::spell),
            Codec.BOOL.optionalFieldOf("immutable", false).forGetter(SpellComponent::immutable),
            Codec.BOOL.optionalFieldOf("closed", false).forGetter(SpellComponent::closed)
    ).apply(instance, SpellComponent::new));

    public SpellComponent(SpellPart spell) {
        this(spell, false, false);
    }

    public SpellComponent(SpellPart spell, boolean immutable) {
        this(spell, immutable, false);
    }

    public SpellComponent withClosed() {
        return new SpellComponent(spell, immutable, true);
    }
}

package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.SpellPart;

public record SpellComponent(SpellPart spell, boolean immutable) {
    public static final Codec<SpellComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellPart.CODEC.fieldOf("spell").forGetter(SpellComponent::spell),
            Codec.BOOL.optionalFieldOf("immutable", false).forGetter(SpellComponent::immutable)
    ).apply(instance, SpellComponent::new));

    public SpellComponent(SpellPart spell) {
        this(spell, false);
    }
}

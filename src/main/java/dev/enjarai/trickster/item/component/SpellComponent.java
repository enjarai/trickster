package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.spell.SpellPart;

public record SpellComponent(SpellPart spell) {
    public static final Codec<SpellComponent> CODEC = SpellPart.CODEC
            .fieldOf("spell").xmap(SpellComponent::new, SpellComponent::spell).codec();
}

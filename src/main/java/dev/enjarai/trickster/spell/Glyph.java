package dev.enjarai.trickster.spell;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.List;
import java.util.Optional;

public sealed interface Glyph permits PatternGlyph, SpellPart {
    Codec<Glyph> CODEC = Codec.either(PatternGlyph.CODEC, SpellPart.CODEC)
            .xmap(e -> e.right().isPresent() ? e.right().orElseThrow() : e.left().orElseThrow(), g -> {
                if (g instanceof PatternGlyph patternGlyph) {
                    return Either.left(patternGlyph);
                }
                return Either.right((SpellPart) g);
            });

    Fragment activateGlyph(SpellContext ctx, List<Optional<Fragment>> fragments);
}

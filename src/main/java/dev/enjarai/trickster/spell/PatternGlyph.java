package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record PatternGlyph(Pattern pattern, List<Byte> orderedPattern) implements Glyph {
    public static final Codec<PatternGlyph> CODEC = Codec.BYTE.listOf().xmap(PatternGlyph::new, PatternGlyph::orderedPattern);

    public PatternGlyph(int... pattern) {
        this(Stream.of(ArrayUtils.toObject(pattern)).map(Integer::byteValue).toList());
    }

    public PatternGlyph(List<Byte> pattern) {
        this(Pattern.from(pattern), pattern);
    }

    @Override
    public Fragment activateGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) {
        return null; // TODO
    }
}

package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record PatternGlyph(List<Byte> pattern) implements Glyph {
    public static final Codec<PatternGlyph> CODEC = Codec.BYTE.listOf(0, 9).comapFlatMap(p -> {
        if (p.stream().anyMatch(b -> b < 0 || b > 8)) {
            return DataResult.error(() -> "Incorrect index value in pattern");
        }
        return DataResult.success(new PatternGlyph(p));
    }, PatternGlyph::pattern);

    public PatternGlyph(int... pattern) {
        this(Stream.of(ArrayUtils.toObject(pattern)).map(Integer::byteValue).toList());
    }

    @Override
    public Fragment activateGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) {
        return null; // TODO
    }
}

package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownTrickBlunder;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record PatternGlyph(Pattern pattern, List<Byte> orderedPattern) implements Fragment {
    public static final Codec<PatternGlyph> CODEC = Codec.BYTE.listOf().xmap(PatternGlyph::new, PatternGlyph::orderedPattern);
    public static final MapCodec<PatternGlyph> MAP_CODEC = CODEC.fieldOf("pattern");

    public PatternGlyph(int... pattern) {
        this(Stream.of(ArrayUtils.toObject(pattern)).map(Integer::byteValue).toList());
    }

    public PatternGlyph(List<Byte> pattern) {
        this(Pattern.from(pattern), pattern);
    }

    @Override
    public Fragment activateAsGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) throws BlunderException {
        if (pattern.equals(Pattern.EMPTY)) {
            return VoidFragment.INSTANCE;
        }

        var trick = Tricks.lookup(pattern);
        if (trick != null) {
            return trick.activate(ctx, fragments.stream().filter(Optional::isPresent).map(Optional::get).toList());
        }
        throw new UnknownTrickBlunder(); // TODO more detail
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.PATTERN;
    }

    @Override
    public Text asText() {
        return Text.of("TODO"); // TODO
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}

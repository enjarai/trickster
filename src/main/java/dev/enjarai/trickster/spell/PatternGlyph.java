package dev.enjarai.trickster.spell;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownTrickBlunder;
import dev.enjarai.trickster.spell.tricks.func.ForkingTrick;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.Stream;

public record PatternGlyph(Pattern pattern) implements Fragment {
    public static final Codec<PatternGlyph> CODEC = Codec.either(Pattern.CODEC, Codec.BYTE.listOf())
            .xmap(
                    either -> either.left().orElseGet(() -> Pattern.from(either.right().orElseThrow())),
                    Either::left
            )
            .xmap(PatternGlyph::new, PatternGlyph::pattern);
    public static final MapCodec<PatternGlyph> MAP_CODEC = CODEC.fieldOf("pattern");

    public PatternGlyph() {
        this(Pattern.EMPTY);
    }

    public PatternGlyph(int... pattern) {
        this(Stream.of(ArrayUtils.toObject(pattern)).map(Integer::byteValue).toList());
    }

    public PatternGlyph(List<Byte> pattern) {
        this(Pattern.from(pattern));
    }

    @Override
    public Fragment activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (pattern.equals(Pattern.EMPTY)) {
            return VoidFragment.INSTANCE;
        }

        var trick = Tricks.lookup(pattern);

        if (trick == null)
            throw new UnknownTrickBlunder();

        return trick.activate(ctx, fragments);
    }

    @Override
    public boolean forks() {
        return Tricks.lookup(pattern) instanceof ForkingTrick;
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var trick = Tricks.lookup(pattern);

        if (trick == null)
            throw new UnknownTrickBlunder();

        return ((ForkingTrick) trick).makeFork(ctx, fragments);
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.PATTERN;
    }

    @Override
    public Text asText() {
        var trick = Tricks.lookup(pattern);
        if (trick != null) {
            return trick.getName();
        }
        return Text.of("Unknown");
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}

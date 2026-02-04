package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.blunder.IncompatibleTypesBlunder;
import dev.enjarai.trickster.spell.blunder.OverweightFragmentBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Tricks;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownTrickBlunder;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.Stream;

public record PatternGlyph(Pattern pattern) implements Fragment, AddableFragment, SubtractableFragment {
    public static final StructEndec<PatternGlyph> ENDEC = StructEndecBuilder.of(
            Pattern.ENDEC.fieldOf("pattern", PatternGlyph::pattern),
            PatternGlyph::new
    );

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
    public EvaluationResult activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (pattern.equals(Pattern.EMPTY)) {
            return VoidFragment.INSTANCE;
        }

        var trick = Tricks.lookup(pattern);

        if (trick == null) {
            throw new UnknownTrickBlunder();
        }

        var restricted = trick.restricted();
        if (restricted != null && ctx.source().getPlayer().map(p -> !restricted.contains(p.getUuid())).orElse(true)) {
            throw new UnknownTrickBlunder();
        }

        var result = trick.activate(ctx, fragments);

        if (result instanceof Fragment fragment && fragment.getWeight() > Fragment.MAX_WEIGHT) {
            throw new OverweightFragmentBlunder(trick, fragment, fragment.getWeight());
        }

        return result;
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
    public int getWeight() {
        return pattern.getWeight();
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof PatternGlyph otherPattern) {
            return new PatternGlyph(pattern().add(otherPattern.pattern()));
        }

        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public SubtractableFragment subtract(Fragment other) throws BlunderException {
        if (other instanceof PatternGlyph otherPattern) {
            return new PatternGlyph(pattern().subtract(otherPattern.pattern()));
        }

        throw new IncompatibleTypesBlunder(Tricks.SUBTRACT);
    }
}

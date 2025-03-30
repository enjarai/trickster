package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AddableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class AddTrick extends DistortionTrick<AddTrick> {
    public AddTrick() {
        super(Pattern.of(7, 4, 0, 1, 2, 4), Signature.of(variadic(AddableFragment.class).require().unpack(), AddTrick::run));
        overload(Signature.of(variadic(FragmentType.PATTERN).require().unpack(), AddTrick::runForGlyphs));
    }

    public Fragment run(SpellContext ctx, List<AddableFragment> fragments) throws BlunderException {
        AddableFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.add(value);
            }
        }

        return result;
    }

    public Fragment runForGlyphs(SpellContext ctx, List<PatternGlyph> patterns) throws BlunderException {
        PatternGlyph result = null;

        for (var value : patterns) {
            if (result == null) {
                result = value;
            } else {
                result = new PatternGlyph(result.pattern().add(value.pattern()));
            }
        }

        return result;
    }
}

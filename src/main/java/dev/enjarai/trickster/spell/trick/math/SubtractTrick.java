package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.SubtractableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class SubtractTrick extends DistortionTrick<SubtractTrick> {
    public SubtractTrick() {
        super(Pattern.of(1, 4, 8, 7, 6, 4), Signature.of(variadic(SubtractableFragment.class).require().unpack(), SubtractTrick::run));
    }

    public Fragment run(SpellContext ctx, List<SubtractableFragment> fragments) throws BlunderException {
        SubtractableFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.subtract(value);
            }
        }

        return result;
    }
}

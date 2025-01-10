package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.DivisibleFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class DivideTrick extends DistortionTrick<DivideTrick> {
    public DivideTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8), Signature.of(variadic(DivisibleFragment.class).required().unpack(), DivideTrick::run));
    }

    public Fragment run(SpellContext ctx, List<DivisibleFragment> fragments) throws BlunderException {
        DivisibleFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.divide(value);
            }
        }

        return result;
    }
}

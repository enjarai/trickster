package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.DivisibleFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class DivideTrick extends DistortionTrick<DivideTrick> {
    public DivideTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8), Signature.of(variadic(DivisibleFragment.class).require().unpack(), DivideTrick::run, RetType.simple(DivisibleFragment.class)));
    }

    public DivisibleFragment run(SpellContext ctx, List<DivisibleFragment> fragments) throws BlunderException {
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

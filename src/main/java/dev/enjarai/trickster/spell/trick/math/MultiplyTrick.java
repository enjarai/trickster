package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.MultiplicableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class MultiplyTrick extends DistortionTrick<MultiplyTrick> {
    public MultiplyTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6), Signature.of(variadic(MultiplicableFragment.class).require().unpack(), MultiplyTrick::run));
    }

    public Fragment run(SpellContext ctx, List<MultiplicableFragment> fragments) throws BlunderException {
        MultiplicableFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.multiply(value);
            }
        }

        return result;
    }
}

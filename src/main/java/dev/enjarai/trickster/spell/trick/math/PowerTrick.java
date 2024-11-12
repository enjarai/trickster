package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class PowerTrick extends DistortionTrick {
    public PowerTrick() {
        super(Pattern.of(6, 1, 8));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var base = expectInput(fragments, NumberFragment.class, 0).number();
        var exponent = expectInput(fragments, NumberFragment.class, 1).number();
        return new NumberFragment(Math.pow(base, exponent));
    }
}

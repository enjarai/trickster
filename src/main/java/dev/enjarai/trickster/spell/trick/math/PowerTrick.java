package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

//TODO:when merging into *-methinks, make this a DistortionTrick
public class PowerTrick extends Trick {
    public PowerTrick() {
        super(Pattern.of(6, 1, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var base = expectInput(fragments, NumberFragment.class, 0).number();
        var exponent = expectInput(fragments, NumberFragment.class, 1).number();
        return new NumberFragment(Math.pow(base, exponent));
    }
}

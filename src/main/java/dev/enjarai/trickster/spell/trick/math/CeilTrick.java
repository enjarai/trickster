package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.RoundableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class CeilTrick extends DistortionTrick {
    public CeilTrick() {
        super(Pattern.of(6, 7, 4, 5));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var param = expectInput(fragments, RoundableFragment.class, 0);

        return param.ceil();
    }
}

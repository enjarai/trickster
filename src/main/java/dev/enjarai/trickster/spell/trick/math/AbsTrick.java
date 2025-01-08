package dev.enjarai.trickster.spell.trick.math;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

public class AbsTrick extends DistortionTrick {
    public AbsTrick() {
        super(Pattern.of(3, 0, 1, 2, 5));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(Math.abs(expectInput(fragments, FragmentType.NUMBER, 0).number()));
    }
}

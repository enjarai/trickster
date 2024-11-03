package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class GreaterThanTrick extends DistortionTrick {
    public GreaterThanTrick() {
        super(Pattern.of(1, 5, 7));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return BooleanFragment.of(expectInput(fragments, FragmentType.NUMBER, 0).number() > expectInput(fragments, FragmentType.NUMBER, 1).number());
    }
}

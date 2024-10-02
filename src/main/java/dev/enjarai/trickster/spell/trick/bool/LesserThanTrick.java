package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class LesserThanTrick extends DistortionTrick {
    public LesserThanTrick() {
        super(Pattern.of(1, 3, 7));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new BooleanFragment(expectInput(fragments, FragmentType.NUMBER, 0).number() < expectInput(fragments, FragmentType.NUMBER, 1).number());
    }
}

package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

import java.util.List;

public class ArcCosTrick extends DistortionTrick {
    public ArcCosTrick() {
        super(Pattern.of(2, 8, 4, 0));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var number = expectInput(fragments, FragmentType.NUMBER, 0);

        return new NumberFragment(Math.acos(number.number()));
    }
}

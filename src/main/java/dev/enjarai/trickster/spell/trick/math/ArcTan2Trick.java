package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

import java.util.List;

public class ArcTan2Trick extends DistortionTrick {
    public ArcTan2Trick() {
        super(Pattern.of(6, 0, 1, 2, 5, 8, 6));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var y = expectInput(fragments, FragmentType.NUMBER, 0);
        var x = expectInput(fragments, FragmentType.NUMBER, 1);

        return new NumberFragment(Math.atan2(y.number(), x.number()));
    }
}

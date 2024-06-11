package dev.enjarai.trickster.spell.tricks.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.RoundableFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class FloorTrick extends Trick {
    public FloorTrick() {
        super(Pattern.of(0, 1, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var param = expectInput(fragments, RoundableFragment.class, 0);

        return param.floor();
    }
}

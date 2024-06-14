package dev.enjarai.trickster.spell.tricks.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class NotEqualsTrick extends Trick {
    public NotEqualsTrick() {
        super(Pattern.of(0, 2, 5, 8, 6, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Fragment last = null;
        for (Fragment fragment : fragments) {
            if (last != null && !fragment.equals(last)) {
                return BooleanFragment.TRUE;
            }
            last = fragment;
        }

        return BooleanFragment.FALSE;
    }
}

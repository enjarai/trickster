package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class NotEqualsTrick extends Trick {
    public NotEqualsTrick() {
        super(Pattern.of(0, 2, 5, 8, 6, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        for (int i = 0; i < fragments.size(); i++) {
            for (int j = 0; j < fragments.size(); j++) {
                if (i == j) continue;

                if (fragments.get(i).equals(fragments.get(j))) {
                    return BooleanFragment.FALSE;
                }
            }
        }

        return BooleanFragment.TRUE;
    }
}

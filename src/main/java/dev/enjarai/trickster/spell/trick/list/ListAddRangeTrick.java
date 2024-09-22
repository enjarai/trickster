package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class ListAddRangeTrick  extends Trick {
    public ListAddRangeTrick() {
        super(Pattern.of(6, 0, 4, 6, 3, 0, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        ListFragment result = null;
        for (int i = 0; i < fragments.size(); i++) {
            var value = expectType(fragments.get(i), ListFragment.class, i);
            if (result == null) {
                result = value;
            } else {
                result = result.addRange(value);
            }
        }

        if (result == null) {
            throw new MissingInputsBlunder(this);
        }

        return result;
    }
}
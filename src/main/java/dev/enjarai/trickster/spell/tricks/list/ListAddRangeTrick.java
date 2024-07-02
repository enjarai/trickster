package dev.enjarai.trickster.spell.tricks.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingInputsBlunder;

import java.util.List;

public class ListAddRangeTrick  extends Trick {
    public ListAddRangeTrick() {
        super(Pattern.of(6, 0, 4, 6, 3, 0, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return fragments.stream()
                .map(l -> expectType(l, ListFragment.class))
                .reduce(ListFragment::addRange)
                .orElseThrow(() -> new MissingInputsBlunder(this));
    }
}
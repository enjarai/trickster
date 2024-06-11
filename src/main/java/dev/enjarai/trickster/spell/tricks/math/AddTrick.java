package dev.enjarai.trickster.spell.tricks.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AddableFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingInputsBlunder;

import java.util.List;

public class AddTrick extends Trick {
    public AddTrick() {
        super(Pattern.of(7, 4, 0, 1, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return fragments.stream()
                .map(a -> expectType(a, AddableFragment.class))
                .reduce(AddableFragment::add)
                .orElseThrow(() -> new MissingInputsBlunder(this));
    }
}

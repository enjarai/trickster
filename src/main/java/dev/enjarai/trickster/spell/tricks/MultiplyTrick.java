package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.MultiplicableFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingInputsBlunder;

import java.util.List;

public class MultiplyTrick extends Trick {
    protected MultiplyTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return fragments.stream()
                .map(a -> expectType(a, MultiplicableFragment.class))
                .reduce(MultiplicableFragment::multiply)
                .orElseThrow(() -> new MissingInputsBlunder(this));
    }
}

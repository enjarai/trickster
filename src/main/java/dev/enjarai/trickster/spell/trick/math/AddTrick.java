package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AddableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.MissingInputsBlunder;

import java.util.List;

public class AddTrick extends Trick {
    public AddTrick() {
        super(Pattern.of(7, 4, 0, 1, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = supposeInput(fragments, 0).flatMap(l -> supposeType(l, FragmentType.LIST));

        if (list.isPresent()) {
            fragments = list.get().fragments();
        }

        return fragments.stream()
                .map(a -> expectType(a, AddableFragment.class))
                .reduce(AddableFragment::add)
                .orElseThrow(() -> new MissingInputsBlunder(this));
    }
}

package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.DivisibleFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class DivideTrick extends Trick {
    public DivideTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        fragments = supposeInput(fragments, 0)
           .flatMap(l -> supposeType(l, FragmentType.LIST))
           .map(ListFragment::fragments)
           .orElse(fragments);

        DivisibleFragment result = null;
        for (int i = 0; i < fragments.size(); i++) {
            var value = expectType(fragments.get(i), DivisibleFragment.class, i);
            if (result == null) {
                result = value;
            } else {
                result = result.divide(value);
            }
        }

        if (result == null) {
            throw new MissingInputsBlunder(this);
        }

        return result;
    }
}

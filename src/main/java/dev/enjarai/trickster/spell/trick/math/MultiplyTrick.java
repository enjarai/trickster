package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.MultiplicableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class MultiplyTrick extends DistortionTrick {
    public MultiplyTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        fragments = supposeInput(fragments, 0)
                .flatMap(l -> supposeType(l, FragmentType.LIST))
                .map(ListFragment::fragments)
                .orElse(fragments);

        MultiplicableFragment result = null;
        for (int i = 0; i < fragments.size(); i++) {
            var value = expectType(fragments.get(i), MultiplicableFragment.class, i);
            if (result == null) {
                result = value;
            } else {
                result = result.multiply(value);
            }
        }

        if (result == null) {
            throw new MissingInputsBlunder(this);
        }

        return result;
    }
}

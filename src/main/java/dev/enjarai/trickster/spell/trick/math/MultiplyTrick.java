package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MultiplicableFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class MultiplyTrick extends Trick {
    public MultiplyTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = supposeInput(fragments, 0).flatMap(l -> supposeType(l, FragmentType.LIST));

        if (list.isPresent()) {
            fragments = list.get().fragments();
        }

        MultiplicableFragment result = null;
        for (int i = 0; i < fragments.size(); i++) {
            var value = expectType(fragments.get(i), MultiplicableFragment.class, list.isPresent() ? 0 : i);
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

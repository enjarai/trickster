package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class EqualsTrick extends DistortionTrick<EqualsTrick> {
    public EqualsTrick() {
        super(Pattern.of(0, 2, 5, 8, 6), Signature.of(ANY_VARIADIC, EqualsTrick::run, FragmentType.BOOLEAN));
    }

    public BooleanFragment run(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Fragment last = null;
        for (Fragment fragment : fragments) {
            if (last != null && !fragment.fuzzyEquals(last)) {
                return BooleanFragment.FALSE;
            }
            last = fragment;
        }

        return BooleanFragment.TRUE;
    }
}

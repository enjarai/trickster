package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class NotEqualsTrick extends DistortionTrick<NotEqualsTrick> {
    public NotEqualsTrick() {
        super(Pattern.of(0, 2, 5, 8, 6, 4, 2), Signature.of(ArgType.ANY.variadicOfArg(), NotEqualsTrick::run, FragmentType.BOOLEAN));
    }

    public BooleanFragment run(SpellContext ctx, List<Fragment> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            for (int j = 0; j < fragments.size(); j++) {
                if (i == j) continue;

                if (fragments.get(i).fuzzyEquals(fragments.get(j))) {
                    return BooleanFragment.FALSE;
                }
            }
        }

        return BooleanFragment.TRUE;
    }
}

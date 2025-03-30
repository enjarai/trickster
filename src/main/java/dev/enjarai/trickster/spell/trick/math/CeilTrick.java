package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.RoundableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class CeilTrick extends DistortionTrick<CeilTrick> {
    public CeilTrick() {
        super(Pattern.of(6, 7, 4, 5), Signature.of(simple(RoundableFragment.class), CeilTrick::math));
    }

    public Fragment math(SpellContext ctx, RoundableFragment param) throws BlunderException {
        return param.ceil();
    }
}

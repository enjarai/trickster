package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.RoundableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class RoundTrick extends DistortionTrick<RoundTrick> {
    public RoundTrick() {
        super(Pattern.of(0, 1, 4, 7, 6), Signature.of(simple(RoundableFragment.class), RoundTrick::math, RetType.simple(RoundableFragment.class)));
    }

    public RoundableFragment math(SpellContext ctx, RoundableFragment param) throws BlunderException {
        return param.round();
    }
}

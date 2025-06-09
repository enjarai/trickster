package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.RoundableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class FloorTrick extends DistortionTrick<FloorTrick> {
    public FloorTrick() {
        super(Pattern.of(0, 1, 4, 5), Signature.of(simple(RoundableFragment.class), FloorTrick::math, RetType.simple(RoundableFragment.class)));
    }

    public RoundableFragment math(SpellContext ctx, RoundableFragment param) throws BlunderException {
        return param.floor();
    }
}

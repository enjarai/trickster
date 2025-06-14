package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class CosTrick extends DistortionTrick<CosTrick> {
    public CosTrick() {
        super(Pattern.of(6, 8, 4, 0), Signature.of(FragmentType.NUMBER, CosTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.cos(number.number()));
    }
}

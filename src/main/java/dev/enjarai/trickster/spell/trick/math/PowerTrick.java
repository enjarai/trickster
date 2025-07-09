package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class PowerTrick extends DistortionTrick<PowerTrick> {
    public PowerTrick() {
        super(Pattern.of(6, 1, 8), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, PowerTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment base, NumberFragment exponent) throws BlunderException {
        return new NumberFragment(Math.pow(base.number(), exponent.number()));
    }
}

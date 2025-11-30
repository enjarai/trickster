package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class ArcTanTrick extends DistortionTrick<ArcTanTrick> {
    public ArcTanTrick() {
        super(Pattern.of(0, 2, 8), Signature.of(FragmentType.NUMBER, ArcTanTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment number) {
        return new NumberFragment(Math.atan(number.number()));
    }
}

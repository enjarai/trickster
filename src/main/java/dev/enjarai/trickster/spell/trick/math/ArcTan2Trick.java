package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class ArcTan2Trick extends DistortionTrick<ArcTan2Trick> {
    public ArcTan2Trick() {
        super(Pattern.of(6, 0, 1, 2, 5, 8, 6), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, ArcTan2Trick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment y, NumberFragment x) {
        return new NumberFragment(Math.atan2(y.number(), x.number()));
    }
}

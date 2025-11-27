package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class TanTrick extends DistortionTrick<TanTrick> {
    public TanTrick() {
        super(Pattern.of(0, 6, 8), Signature.of(FragmentType.NUMBER, TanTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment number) {
        return new NumberFragment(Math.tan(number.number()));
    }
}

package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class ArcCosTrick extends DistortionTrick<ArcCosTrick> {
    public ArcCosTrick() {
        super(Pattern.of(2, 8, 4, 0), Signature.of(FragmentType.NUMBER, ArcCosTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.acos(number.number()));
    }
}

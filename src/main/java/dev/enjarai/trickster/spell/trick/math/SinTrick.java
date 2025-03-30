package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class SinTrick extends DistortionTrick<SinTrick> {
    public SinTrick() {
        super(Pattern.of(6, 0, 4, 8), Signature.of(FragmentType.NUMBER, SinTrick::math));
    }

    public Fragment math(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.sin(number.number()));
    }
}

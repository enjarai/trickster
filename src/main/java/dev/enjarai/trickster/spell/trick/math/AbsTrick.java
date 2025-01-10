package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class AbsTrick extends DistortionTrick<AbsTrick> {
    public AbsTrick() {
        super(Pattern.of(3, 0, 1, 2, 5), Signature.of(FragmentType.NUMBER, AbsTrick::math));
    }

    public Fragment math(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.abs(number.number()));
    }
}

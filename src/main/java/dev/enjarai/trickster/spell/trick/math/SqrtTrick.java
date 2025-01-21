package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class SqrtTrick extends DistortionTrick<SqrtTrick> {
    public SqrtTrick() {
        super(Pattern.of(3, 4, 7, 2), Signature.of(FragmentType.NUMBER, SqrtTrick::math));
    }

    public Fragment math(SpellContext ctx, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.sqrt(number.number()));
    }
}

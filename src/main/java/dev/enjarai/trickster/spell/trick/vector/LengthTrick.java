package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class LengthTrick extends DistortionTrick<LengthTrick> {
    public LengthTrick() {
        super(Pattern.of(3, 4, 5, 2, 4, 1), Signature.of(FragmentType.VECTOR, LengthTrick::math));
    }

    public Fragment math(SpellContext ctx, VectorFragment vec) throws BlunderException {
        return new NumberFragment(vec.vector().length());
    }
}

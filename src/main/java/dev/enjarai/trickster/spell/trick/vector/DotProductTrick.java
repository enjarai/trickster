package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class DotProductTrick extends DistortionTrick<DotProductTrick> {
    public DotProductTrick() {
        super(Pattern.of(4, 3, 0, 1, 2, 5, 8, 4), Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, DotProductTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, VectorFragment vec1, VectorFragment vec2) {
        return new NumberFragment(vec1.vector().dot(vec2.vector()));
    }
}

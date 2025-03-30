package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

public class CrossProductTrick extends DistortionTrick<CrossProductTrick> {
    public CrossProductTrick() {
        super(Pattern.of(0, 4, 8, 6, 4, 2), Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, CrossProductTrick::math));
    }

    public Fragment math(SpellContext ctx, VectorFragment vec1, VectorFragment vec2) throws BlunderException {
        return new VectorFragment(vec1.vector().cross(vec2.vector(), new Vector3d()));
    }
}

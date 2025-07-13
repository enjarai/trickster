package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

public class NormalizeTrick extends DistortionTrick<NormalizeTrick> {
    public NormalizeTrick() {
        super(Pattern.of(3, 4, 5, 6, 3), Signature.of(FragmentType.VECTOR, NormalizeTrick::normalize, FragmentType.VECTOR));
    }

    public VectorFragment normalize(SpellContext ctx, VectorFragment vec) throws BlunderException {
        return new VectorFragment(vec.vector().normalize(new Vector3d()));
    }
}

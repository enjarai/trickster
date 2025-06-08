package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

public class AlignVectorTrick extends DistortionTrick<AlignVectorTrick> {
    public AlignVectorTrick() {
        super(Pattern.of(6, 4, 1, 2, 4, 5), Signature.of(FragmentType.VECTOR, AlignVectorTrick::align, FragmentType.VECTOR));
    }

    public VectorFragment align(SpellContext ctx, VectorFragment vec) throws BlunderException {
        var vector = vec.vector();
        var absX = Math.abs(vector.x());
        var absY = Math.abs(vector.y());
        var absZ = Math.abs(vector.z());

        if (absX == 0 && absY == 0 && absZ == 0) {
            return VectorFragment.ZERO;
        }

        if (absX >= absY && absX >= absZ) {
            return new VectorFragment(new Vector3d(vector.x(), 0, 0).normalize());
        } else if (absY >= absX && absY >= absZ) {
            return new VectorFragment(new Vector3d(0, vector.y(), 0).normalize());
        } else {
            return new VectorFragment(new Vector3d(0, 0, vector.z()).normalize());
        }
    }
}

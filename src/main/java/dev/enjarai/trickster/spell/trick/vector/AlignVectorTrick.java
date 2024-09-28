package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class AlignVectorTrick extends Trick {
    public AlignVectorTrick() {
        super(Pattern.of(6, 4, 1, 2, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vec = expectInput(fragments, FragmentType.VECTOR, 0);

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

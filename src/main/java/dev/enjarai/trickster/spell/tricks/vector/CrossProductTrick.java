package dev.enjarai.trickster.spell.tricks.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class CrossProductTrick extends Trick {
    public CrossProductTrick() {
        super(Pattern.of(0, 4, 8, 6, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vec1 = expectInput(fragments, FragmentType.VECTOR, 0);
        var vec2 = expectInput(fragments, FragmentType.VECTOR, 1);

        return new VectorFragment(vec1.vector().cross(vec2.vector(), new Vector3d()));
    }
}

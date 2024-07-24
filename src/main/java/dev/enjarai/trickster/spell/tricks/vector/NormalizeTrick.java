package dev.enjarai.trickster.spell.tricks.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class NormalizeTrick extends Trick {
    public NormalizeTrick() {
        super(Pattern.of(3, 4, 5, 6, 3));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var vec = expectInput(fragments, FragmentType.VECTOR, 0);

        return new VectorFragment(vec.vector().normalize(new Vector3d()));
    }
}

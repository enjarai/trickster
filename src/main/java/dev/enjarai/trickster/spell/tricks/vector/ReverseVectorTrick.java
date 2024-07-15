package dev.enjarai.trickster.spell.tricks.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class ReverseVectorTrick extends Trick {
    public ReverseVectorTrick() {
        super(Pattern.of(3, 4, 5, 2, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vec = expectInput(fragments, FragmentType.VECTOR, 0);

        return new VectorFragment(vec.vector().mul(-1, new Vector3d()));
    }
}

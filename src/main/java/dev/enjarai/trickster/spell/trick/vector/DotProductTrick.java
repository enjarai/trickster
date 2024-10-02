package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class DotProductTrick extends DistortionTrick {
    public DotProductTrick() {
        super(Pattern.of(4, 3, 0, 1, 2, 5, 8, 4));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var vec1 = expectInput(fragments, FragmentType.VECTOR, 0);
        var vec2 = expectInput(fragments, FragmentType.VECTOR, 1);

        return new NumberFragment(vec1.vector().dot(vec2.vector()));
    }
}

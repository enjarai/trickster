package dev.enjarai.trickster.spell.tricks.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class DotProductTrick extends Trick {
    public DotProductTrick() {
        super(Pattern.of(4, 3, 0, 1, 2, 5, 8, 4));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var vec1 = expectInput(fragments, FragmentType.VECTOR, 0);
        var vec2 = expectInput(fragments, FragmentType.VECTOR, 1);

        return new NumberFragment(vec1.vector().dot(vec2.vector()));
    }
}

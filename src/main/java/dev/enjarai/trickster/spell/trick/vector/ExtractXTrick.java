package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ExtractXTrick extends Trick {
    public ExtractXTrick() {
        super(Pattern.of(0, 3, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(expectInput(fragments, FragmentType.VECTOR, 0).vector().x());
    }
}

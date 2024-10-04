package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ListSizeTrick extends DistortionTrick {
    public ListSizeTrick() {
        super(Pattern.of(0, 2, 5, 4, 3, 0));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(expectInput(fragments, FragmentType.LIST, 0).fragments().size());
    }
}

package dev.enjarai.trickster.spell.trick.list;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

public class ListReverseTrick extends DistortionTrick {
    public ListReverseTrick() {
        super(Pattern.of(2, 0, 3, 6, 8));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ListFragment(expectInput(fragments, FragmentType.LIST, 0).fragments().reversed());
    }
}

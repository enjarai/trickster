package dev.enjarai.trickster.spell.tricks.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class ListIndexOfTrick extends Trick {
    public ListIndexOfTrick() {
        super(Pattern.of(8, 5, 2, 0, 3, 6, 4, 2, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var el = expectInput(fragments, 1);

        var index = list.fragments().indexOf(el);

        return index == -1 ? VoidFragment.INSTANCE : new NumberFragment(index);
    }
}

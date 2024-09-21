package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;

public class ListRemoveElementTrick extends Trick {
    public ListRemoveElementTrick() {
        super(Pattern.of(4, 6, 3, 0, 4, 8, 5, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var toRemove = fragments.subList(1, fragments.size());

        var newList = new ArrayList<Fragment>(list.fragments().size());
        newList.addAll(list.fragments());
        newList.removeAll(toRemove);
        return new ListFragment(ImmutableList.copyOf(newList));
    }
}

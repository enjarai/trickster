package dev.enjarai.trickster.spell.tricks.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;

public class ListInsertTrick extends Trick {
    public ListInsertTrick() {
        super(Pattern.of(6, 3, 0, 4, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var index = expectInput(fragments, FragmentType.NUMBER, 1);
        var el = expectInput(fragments, 2);

        var newList = new ArrayList<Fragment>(list.fragments().size() + 1);
        newList.addAll(list.fragments());
        newList.add((int) Math.floor(index.number()), el);
        return new ListFragment(ImmutableList.copyOf(newList));
    }
}

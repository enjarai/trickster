package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;

public class ListRemoveElementTrick extends DistortionTrick<ListRemoveElementTrick> {
    public ListRemoveElementTrick() {
        super(Pattern.of(4, 6, 3, 0, 4, 8, 5, 2), Signature.of(FragmentType.LIST, ANY_VARIADIC, ListRemoveElementTrick::run));
    }

    public Fragment run(SpellContext ctx, ListFragment list, List<Fragment> toRemove) throws BlunderException {
        var newList = new ArrayList<Fragment>(list.fragments().size());
        newList.addAll(list.fragments());
        newList.removeAll(toRemove);
        return new ListFragment(ImmutableList.copyOf(newList));
    }
}

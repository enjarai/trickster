package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ListAddRangeTrick extends DistortionTrick<ListAddRangeTrick> {
    public ListAddRangeTrick() {
        super(Pattern.of(6, 0, 4, 6, 3, 0, 2, 5, 8), Signature.of(FragmentType.LIST, variadic(FragmentType.LIST), ListAddRangeTrick::add));
    }

    public Fragment add(SpellContext ctx, ListFragment baseList, List<ListFragment> lists) throws BlunderException {
        for (ListFragment list : lists) {
            baseList = baseList.addRange(list);
        }

        return baseList;
    }
}

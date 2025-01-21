package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class ListIndexOfTrick extends DistortionTrick<ListIndexOfTrick> {
    public ListIndexOfTrick() {
        super(Pattern.of(8, 5, 2, 0, 3, 6, 4, 2, 1), Signature.of(FragmentType.LIST, ANY, ListIndexOfTrick::indexOf));
    }

    public Fragment indexOf(SpellContext ctx, ListFragment list, Fragment el) throws BlunderException {
        var index = list.fragments().indexOf(el);

        return index == -1 ? VoidFragment.INSTANCE : new NumberFragment(index);
    }
}

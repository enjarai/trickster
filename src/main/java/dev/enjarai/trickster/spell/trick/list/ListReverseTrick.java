package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class ListReverseTrick extends DistortionTrick<ListReverseTrick> {
    public ListReverseTrick() {
        super(Pattern.of(2, 0, 3, 6, 8), Signature.of(FragmentType.LIST, ListReverseTrick::reverse));
    }

    public Fragment reverse(SpellContext ctx, ListFragment list) throws BlunderException {
        return new ListFragment(list.fragments().reversed());
    }
}

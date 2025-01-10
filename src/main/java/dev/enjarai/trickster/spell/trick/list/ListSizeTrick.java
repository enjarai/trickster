package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class ListSizeTrick extends DistortionTrick<ListSizeTrick> {
    public ListSizeTrick() {
        super(Pattern.of(0, 2, 5, 4, 3, 0), Signature.of(FragmentType.LIST, ListSizeTrick::run));
    }

    public Fragment run(SpellContext ctx, ListFragment list) throws BlunderException {
        return new NumberFragment(list.fragments().size());
    }
}

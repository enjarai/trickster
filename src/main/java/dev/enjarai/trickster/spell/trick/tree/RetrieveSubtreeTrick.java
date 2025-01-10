package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class RetrieveSubtreeTrick extends AbstractMetaTrick<RetrieveSubtreeTrick> {
    public RetrieveSubtreeTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8, 4, 0, 1, 2), Signature.of(FragmentType.SPELL_PART, FragmentType.LIST, RetrieveSubtreeTrick::retrieve));
    }

    public Fragment retrieve(SpellContext ctx, SpellPart spell, ListFragment addressFragment) throws BlunderException {
        return findNode(spell, addressFragment).<Fragment>map(n -> n).orElse(VoidFragment.INSTANCE);
    }
}

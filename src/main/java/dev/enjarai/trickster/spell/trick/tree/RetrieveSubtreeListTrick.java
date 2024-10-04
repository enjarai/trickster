package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class RetrieveSubtreeListTrick extends AbstractMetaTrick {
    public RetrieveSubtreeListTrick() {
        super(Pattern.of(5, 4, 3, 0, 1, 2, 5, 8, 7, 4, 1));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

    return findNode(spell, addressFragment)
        .<Fragment>map(n -> new ListFragment(n.subParts))
        .orElse(VoidFragment.INSTANCE);
    }
}

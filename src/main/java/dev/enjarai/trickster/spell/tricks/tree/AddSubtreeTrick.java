package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class AddSubtreeTrick extends Trick {
    public AddSubtreeTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6, 4, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);
        var subtree = expectInput(fragments, SpellPart.class, 2);

        var address = addressFragment.sanitizeAddress(this);
        var newSpell = spell.deepClone();

        var node = newSpell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index) {
                node = subParts.get(index);
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        node.subParts.add(subtree);

        return newSpell;
    }
}

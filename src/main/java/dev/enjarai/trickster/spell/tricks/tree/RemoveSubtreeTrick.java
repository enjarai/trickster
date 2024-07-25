package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class RemoveSubtreeTrick extends MetaTrick {
    public RemoveSubtreeTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2, 4, 6, 7, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        var address = addressFragment.sanitizeAddress(this);
        var newSpell = spell.deepClone();

        SpellPart prev = null;
        var node = newSpell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index) {
                var newNode = subParts.get(index);
                prev = node;
                node = newNode;
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        if (prev == null) {
            return VoidFragment.INSTANCE;
        } else {
            prev.subParts.remove(address.getLast().intValue());
            return newSpell;
        }
    }
}

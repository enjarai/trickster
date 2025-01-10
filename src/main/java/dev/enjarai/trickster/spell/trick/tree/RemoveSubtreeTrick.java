package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class RemoveSubtreeTrick extends AbstractMetaTrick<RemoveSubtreeTrick> {
    public RemoveSubtreeTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2, 4, 6, 7, 8), Signature.of(FragmentType.SPELL_PART, FragmentType.LIST, RemoveSubtreeTrick::remove));
    }

    public Fragment remove(SpellContext ctx, SpellPart spell, ListFragment addressFragment) throws BlunderException {
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

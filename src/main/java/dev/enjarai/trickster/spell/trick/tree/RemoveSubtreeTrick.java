package dev.enjarai.trickster.spell.trick.tree;

import java.util.List;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class RemoveSubtreeTrick extends AbstractMetaTrick<RemoveSubtreeTrick> {
    public RemoveSubtreeTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2, 4, 6, 7, 8), Signature.of(FragmentType.SPELL_PART, ADDRESS, RemoveSubtreeTrick::remove));
    }

    public Fragment remove(SpellContext ctx, SpellPart spell, List<NumberFragment> address) throws BlunderException {
        var newSpell = spell.deepClone();

        SpellPart prev = null;
        var node = newSpell;
        for (var index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index.asInt()) {
                var newNode = subParts.get(index.asInt());
                prev = node;
                node = newNode;
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        if (prev == null) {
            return VoidFragment.INSTANCE;
        } else {
            prev.subParts.remove(address.getLast().asInt());
            return newSpell;
        }
    }
}

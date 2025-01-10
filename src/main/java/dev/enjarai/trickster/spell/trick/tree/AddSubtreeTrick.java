package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class AddSubtreeTrick extends AbstractMetaTrick<AddSubtreeTrick> {
    public AddSubtreeTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6, 4, 2, 5, 8), Signature.of(FragmentType.SPELL_PART, FragmentType.LIST, FragmentType.SPELL_PART, AddSubtreeTrick::add));
    }

    public Fragment add(SpellContext ctx, SpellPart spell, ListFragment addressFragment, SpellPart subtree) throws BlunderException {
        var newSpell = spell.deepClone();
        var node = findNode(newSpell, addressFragment)
                .orElseThrow(() -> new AddressNotInTreeBlunder(this, addressFragment.sanitizeAddress(this)));
        node.subParts.add(subtree);

        return newSpell;
    }
}

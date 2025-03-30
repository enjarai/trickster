package dev.enjarai.trickster.spell.trick.tree;

import java.util.List;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class AddSubtreeTrick extends AbstractMetaTrick<AddSubtreeTrick> {
    public AddSubtreeTrick() {
        super(Pattern.of(2, 1, 0, 4, 8, 7, 6, 4, 2, 5, 8), Signature.of(FragmentType.SPELL_PART, ADDRESS, FragmentType.SPELL_PART, AddSubtreeTrick::add));
    }

    public Fragment add(SpellContext ctx, SpellPart spell, List<NumberFragment> address, SpellPart subtree) throws BlunderException {
        var newSpell = spell.deepClone();
        var node = findNode(newSpell, address)
                .orElseThrow(() -> new AddressNotInTreeBlunder(this, address));
        node.subParts.add(subtree);

        return newSpell;
    }
}

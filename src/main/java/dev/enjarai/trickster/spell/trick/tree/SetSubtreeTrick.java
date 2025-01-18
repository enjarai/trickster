package dev.enjarai.trickster.spell.trick.tree;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class SetSubtreeTrick extends AbstractMetaTrick<SetSubtreeTrick> {
    public SetSubtreeTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8, 4, 0, 3, 6), Signature.of(FragmentType.SPELL_PART, ADDRESS, FragmentType.SPELL_PART, SetSubtreeTrick::set));
    }

    public Fragment set(SpellContext ctx, SpellPart spell, List<NumberFragment> address, SpellPart subTree) throws BlunderException {
        var newSpell = spell.deepClone();
        var node = findNode(newSpell, address)
                .orElseThrow(() -> new AddressNotInTreeBlunder(this, address));
        node.glyph = subTree.glyph;
        node.subParts = subTree.subParts;

        return newSpell;
    }
}

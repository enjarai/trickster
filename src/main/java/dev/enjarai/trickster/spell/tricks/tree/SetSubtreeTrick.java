package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class SetSubtreeTrick extends Trick {
    public SetSubtreeTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8, 4, 0, 3, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);
        var subTree = expectInput(fragments, SpellPart.class, 2);

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
        node.glyph = subTree.glyph;
        node.subParts = subTree.subParts;

        return newSpell;
    }

}

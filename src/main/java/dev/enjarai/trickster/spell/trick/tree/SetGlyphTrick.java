package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class SetGlyphTrick extends AbstractMetaTrick<SetGlyphTrick> {
    public SetGlyphTrick() {
        super(Pattern.of(0, 1, 2, 4, 8, 7, 6), Signature.of(FragmentType.SPELL_PART, FragmentType.LIST, ANY, SetGlyphTrick::set));
    }

    public Fragment set(SpellContext ctx, SpellPart spell, ListFragment addressFragment, Fragment glyph) throws BlunderException {
        var newSpell = spell.deepClone();
        var node = findNode(newSpell, addressFragment)
                .orElseThrow(() -> new AddressNotInTreeBlunder(this, addressFragment.sanitizeAddress(this)));
        node.glyph = glyph;

        return newSpell;
    }
}

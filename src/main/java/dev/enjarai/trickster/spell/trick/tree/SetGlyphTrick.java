package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class SetGlyphTrick extends AbstractMetaTrick<SetGlyphTrick> {
    public SetGlyphTrick() {
        super(Pattern.of(0, 1, 2, 4, 8, 7, 6), Signature.of(FragmentType.SPELL_PART, ADDRESS, ANY, SetGlyphTrick::set, FragmentType.SPELL_PART));
    }

    public SpellPart set(SpellContext ctx, SpellPart spell, List<NumberFragment> address, Fragment glyph) throws BlunderException {
        var newSpell = spell.deepClone();
        var node = findNode(newSpell, address)
                .orElseThrow(() -> new AddressNotInTreeBlunder(this, address));
        node.glyph = glyph;

        return newSpell;
    }
}

package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class RetrieveGlyphTrick extends AbstractMetaTrick<RetrieveGlyphTrick> {
    public RetrieveGlyphTrick() {
        super(Pattern.of(2, 1, 0, 4, 6, 7, 8), Signature.of(FragmentType.SPELL_PART, ADDRESS, RetrieveGlyphTrick::retrieve, RetType.ANY.optionalOfRet()));
    }

    public Optional<Fragment> retrieve(SpellContext ctx, SpellPart spell, List<NumberFragment> address) throws BlunderException {
        return findNode(spell, address).map(node -> node.glyph);
    }
}

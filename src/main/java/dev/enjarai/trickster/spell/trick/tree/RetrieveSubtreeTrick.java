package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class RetrieveSubtreeTrick extends AbstractMetaTrick<RetrieveSubtreeTrick> {
    public RetrieveSubtreeTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8, 4, 0, 1, 2), Signature.of(FragmentType.SPELL_PART, ADDRESS, RetrieveSubtreeTrick::retrieve, FragmentType.SPELL_PART.optionalOfRet()));
    }

    public Optional<SpellPart> retrieve(SpellContext ctx, SpellPart spell, List<NumberFragment> address) {
        return findNode(spell, address);
    }
}

package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class GetSubPartsTrick extends AbstractMetaTrick<GetSubPartsTrick> {
    public GetSubPartsTrick() {
        super(Pattern.of(6, 4, 2, 5, 8, 4, 0, 3, 6, 8), Signature.of(FragmentType.SPELL_PART, GetSubPartsTrick::get, FragmentType.SPELL_PART.listOfRet()));
    }

    public List<SpellPart> get(SpellContext ctx, SpellPart spell) {
        return spell.subParts;
    }
}

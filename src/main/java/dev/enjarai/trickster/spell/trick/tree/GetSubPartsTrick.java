package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class GetSubPartsTrick extends AbstractMetaTrick<GetSubPartsTrick> {
    public GetSubPartsTrick() {
        super(Pattern.of(6, 4, 2, 5, 8, 4, 0, 3, 6, 8), Signature.of(FragmentType.SPELL_PART, GetSubPartsTrick::get));
    }

    public Fragment get(SpellContext ctx, SpellPart spell) throws BlunderException {
        return new ListFragment(spell.subParts.stream().<Fragment>map(n -> n).toList());
    }
}

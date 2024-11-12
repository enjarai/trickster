package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class GetSubPartsTrick extends AbstractMetaTrick {
    public GetSubPartsTrick() {
        super(Pattern.of(6, 4, 2, 5, 8, 4, 0, 3, 6, 8));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        return new ListFragment(spell.subParts.stream().<Fragment>map(n -> n).toList());
    }
}

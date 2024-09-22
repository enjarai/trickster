package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class AllTrick extends Trick {
    public AllTrick() {
        super(Pattern.of(1, 4, 7, 5, 4, 3, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new BooleanFragment(fragments.stream().allMatch(f -> f.asBoolean().bool()));
    }
}

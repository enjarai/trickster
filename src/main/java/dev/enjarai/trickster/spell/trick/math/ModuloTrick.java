package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class ModuloTrick extends Trick {
    public ModuloTrick() {
        super(Pattern.of(0, 4, 1, 2, 4, 6, 7, 4, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var param1 = expectInput(fragments, FragmentType.NUMBER, 0);
        var param2 = expectInput(fragments, FragmentType.NUMBER, 1);

        return new NumberFragment(param1.number() % param2.number());
    }
}

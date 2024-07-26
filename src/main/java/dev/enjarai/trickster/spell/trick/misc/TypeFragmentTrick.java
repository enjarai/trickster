package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.TypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class TypeFragmentTrick extends Trick {
    public TypeFragmentTrick() {
        super(Pattern.of(3, 4, 0, 1, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var fragment = expectInput(fragments, 0);

        return new TypeFragment(fragment.type());
    }
}

package dev.enjarai.trickster.spell.tricks.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.TypeFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

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

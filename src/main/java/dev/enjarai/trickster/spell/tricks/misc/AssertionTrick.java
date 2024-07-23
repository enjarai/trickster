package dev.enjarai.trickster.spell.tricks.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.AssertionBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class AssertionTrick extends Trick {
    public AssertionTrick() {
        super(Pattern.of(3, 6, 8, 4, 0, 2, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var result = expectInput(fragments, 0);

        if (!expectInput(fragments, 1).asBoolean().bool())
            throw new AssertionBlunder(this, result);

        return result;
    }
}

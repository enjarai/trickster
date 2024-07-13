package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class CasterReflectionTrick extends Trick {
    public CasterReflectionTrick() {
        super(Pattern.of(4, 5, 7, 3, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.getCaster().map(EntityFragment::from)
                .orElseThrow(() -> new NoPlayerBlunder(this));
    }
}

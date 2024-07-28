package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;

import java.util.List;

public class CasterReflectionTrick extends Trick {
    public CasterReflectionTrick() {
        super(Pattern.of(4, 5, 7, 3, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.source().getCaster().map(EntityFragment::from)
                .orElseThrow(() -> new NoPlayerBlunder(this));
    }
}

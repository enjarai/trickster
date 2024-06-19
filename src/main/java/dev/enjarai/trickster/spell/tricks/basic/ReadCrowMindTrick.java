package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class ReadCrowMindTrick extends Trick {
    public ReadCrowMindTrick() {
        super(Pattern.of(5, 8, 6, 3, 4, 0, 1, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.getCrowMind();
    }
}

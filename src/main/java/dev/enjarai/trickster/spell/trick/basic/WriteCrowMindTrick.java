package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class WriteCrowMindTrick extends Trick {
    public WriteCrowMindTrick() {
        super(Pattern.of(3, 6, 8, 5, 4, 0, 1, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var fragment = expectInput(fragments, 0);

        ctx.source().setCrowMind(fragment);
        return fragment;
    }
}

package dev.enjarai.trickster.spell.trick.func;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class GetCurrentThreadTrick extends Trick {
    public GetCurrentThreadTrick() {
        super(Pattern.of(1, 2, 7, 0, 1, 8, 7, 6, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(ctx.data().getSlot());
    }
}

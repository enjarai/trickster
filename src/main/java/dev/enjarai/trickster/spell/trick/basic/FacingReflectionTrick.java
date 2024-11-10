package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;

import java.util.List;

public class FacingReflectionTrick extends Trick {
    public FacingReflectionTrick() {
        super(Pattern.of(3, 1, 5, 7, 3, 6, 4, 2, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new VectorFragment(ctx.source().getFacing().orElseThrow(() -> new IncompatibleSourceBlunder(this)));
    }
}

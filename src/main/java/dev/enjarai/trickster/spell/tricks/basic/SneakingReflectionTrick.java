package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class SneakingReflectionTrick extends Trick {
    public SneakingReflectionTrick() {
        super(Pattern.of(0, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new BooleanFragment(ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)).isSneaking());
    }
}

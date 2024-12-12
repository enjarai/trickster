package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;

import java.util.List;

public class HotbarReflectionTrick extends Trick<HotbarReflectionTrick> {
    public HotbarReflectionTrick() {
        super(Pattern.of(6, 4, 8, 5, 4, 3, 6, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(ctx.source().getPlayer()
                .orElseThrow(() -> new NoPlayerBlunder(this)).getInventory().selectedSlot);
    }
}

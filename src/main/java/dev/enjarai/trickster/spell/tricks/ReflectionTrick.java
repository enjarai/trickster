package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class ReflectionTrick extends Trick {
    public ReflectionTrick() {
        super(Pattern.of(1, 5, 7, 3, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.getPlayer()
                .map(player -> new EntityFragment(player.getUuid(), player.getName()))
                .orElseThrow(() -> new NoPlayerBlunder(this));
    }
}

package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class BlockingReflectionTrick extends AbstractLivingEntityQueryTrick {
    public BlockingReflectionTrick() {
        super(Pattern.of(0, 2, 8, 7, 6, 0, 4, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return BooleanFragment.of(getLivingEntity(ctx, fragments, 0).isBlocking());
    }
}

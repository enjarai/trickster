package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class SneakingReflectionTrick extends AbstractLivingEntityQueryTrick {
    public SneakingReflectionTrick() {
        super(Pattern.of(2, 4, 7));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        return new BooleanFragment(getLivingEntity(ctx, fragments, 0).isSneaking());
    }
}

package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class GetEntityMaxHealthTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityMaxHealthTrick() {
        super(Pattern.of(1, 6, 8, 1, 4, 0, 3, 7, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(getLivingEntity(ctx, fragments, 0).getMaxHealth());
    }
}

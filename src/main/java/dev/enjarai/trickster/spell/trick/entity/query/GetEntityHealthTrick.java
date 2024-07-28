package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class GetEntityHealthTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityHealthTrick() {
        super(Pattern.of(4, 0, 3, 7, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(getLivingEntity(ctx, fragments, 0).getHealth());
    }
}

package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class GetEntityArmourTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityArmourTrick() {
        super(Pattern.of(1, 3, 4, 0, 3, 7, 6, 4, 7, 8, 4, 1, 5, 2, 4, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(getLivingEntity(ctx, fragments, 0).getArmor());
    }
}

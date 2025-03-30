package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GetEntityHealthTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityHealthTrick() {
        super(Pattern.of(4, 0, 3, 7, 5, 2, 4));
    }

    @Override
    public Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return new NumberFragment(entity.getHealth());
    }
}

package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class SneakingReflectionTrick extends AbstractLivingEntityQueryTrick {
    public SneakingReflectionTrick() {
        super(Pattern.of(2, 4, 7));
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isSneaking());
    }
}

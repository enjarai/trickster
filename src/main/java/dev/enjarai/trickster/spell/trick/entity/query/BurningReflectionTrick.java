package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.entity.LivingEntity;

public class BurningReflectionTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public BurningReflectionTrick() {
        super(Pattern.of(3, 6, 8, 5, 1, 3, 0, 4, 2, 5), LivingEntity.class);
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isOnFire());
    }
}

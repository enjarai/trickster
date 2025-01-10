package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class SprintingReflectionTrick extends AbstractLivingEntityQueryTrick {
    public SprintingReflectionTrick() {
        super(Pattern.of(6, 8, 4, 5));
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isSprinting());
    }
}

package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.entity.LivingEntity;

public class SprintingReflectionTrick extends AbstractEntityQueryTrick<LivingEntity, BooleanFragment> {
    public SprintingReflectionTrick() {
        super(Pattern.of(6, 8, 4, 5), LivingEntity.class, FragmentType.BOOLEAN);
    }

    @Override
    protected BooleanFragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isSprinting());
    }
}

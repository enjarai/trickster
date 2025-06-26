package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.entity.LivingEntity;

public class BlockingReflectionTrick extends AbstractEntityQueryTrick<LivingEntity, BooleanFragment> {
    public BlockingReflectionTrick() {
        super(Pattern.of(0, 2, 8, 7, 6, 0, 4, 8), LivingEntity.class, FragmentType.BOOLEAN);
    }

    @Override
    protected BooleanFragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isBlocking());
    }
}

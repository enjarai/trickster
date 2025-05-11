package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class BlockingReflectionTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public BlockingReflectionTrick() {
        super(Pattern.of(0, 2, 8, 7, 6, 0, 4, 8), LivingEntity.class);
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return BooleanFragment.of(entity.isBlocking());
    }
}

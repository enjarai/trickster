package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GetEntityMaxHealthTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public GetEntityMaxHealthTrick() {
        super(Pattern.of(1, 6, 8, 1, 4, 0, 3, 7, 5, 2, 4), LivingEntity.class);
    }

    @Override
    public Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return new NumberFragment(entity.getMaxHealth());
    }
}

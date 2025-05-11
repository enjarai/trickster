package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GetEntityArmourTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public GetEntityArmourTrick() {
        super(Pattern.of(1, 3, 4, 0, 3, 7, 6, 4, 7, 8, 4, 1, 5, 2, 4, 5, 7), LivingEntity.class);
    }

    @Override
    public Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return new NumberFragment(entity.getArmor());
    }
}

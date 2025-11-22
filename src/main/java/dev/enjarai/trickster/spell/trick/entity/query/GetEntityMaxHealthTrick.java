package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;

public class GetEntityMaxHealthTrick extends AbstractEntityQueryTrick<LivingEntity, NumberFragment> {
    public GetEntityMaxHealthTrick() {
        super(Pattern.of(1, 6, 8, 1, 4, 0, 3, 7, 5, 2, 4), LivingEntity.class, FragmentType.NUMBER);
    }

    @Override
    public NumberFragment run(SpellContext ctx, LivingEntity entity) {
        return new NumberFragment(entity.getMaxHealth());
    }
}

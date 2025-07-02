package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;

public class GetEntityHealthTrick extends AbstractEntityQueryTrick<LivingEntity, NumberFragment> {
    public GetEntityHealthTrick() {
        super(Pattern.of(4, 0, 3, 7, 5, 2, 4), LivingEntity.class, FragmentType.NUMBER);
    }

    @Override
    public NumberFragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return new NumberFragment(entity.getHealth());
    }
}

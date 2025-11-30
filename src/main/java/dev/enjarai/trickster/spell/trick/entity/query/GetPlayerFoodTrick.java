package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.player.PlayerEntity;

public class GetPlayerFoodTrick extends AbstractEntityQueryTrick<PlayerEntity, NumberFragment> {
    public GetPlayerFoodTrick() {
        super(Pattern.of(6, 4, 1, 2, 5, 4), PlayerEntity.class, FragmentType.NUMBER);
    }

    @Override
    protected NumberFragment run(SpellContext ctx, PlayerEntity entity) {
        return new NumberFragment(entity.getHungerManager().getFoodLevel());
    }
}

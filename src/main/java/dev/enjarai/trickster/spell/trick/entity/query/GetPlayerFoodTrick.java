package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.player.PlayerEntity;

public class GetPlayerFoodTrick extends AbstractEntityQueryTrick<PlayerEntity> {
    public GetPlayerFoodTrick() {
        super(Pattern.of(6, 4, 1, 2, 5, 4), PlayerEntity.class);
    }

    @Override
    protected Fragment run(SpellContext ctx, PlayerEntity entity) throws BlunderException {
        return new NumberFragment(entity.getHungerManager().getFoodLevel());
    }
}

package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.player.PlayerEntity;

public class GetPlayerSaturationTrick extends AbstractEntityQueryTrick<PlayerEntity, NumberFragment> {
    public GetPlayerSaturationTrick() {
        super(Pattern.of(6, 4, 1, 2, 5, 4, 2), PlayerEntity.class, FragmentType.NUMBER);
    }

    @Override
    protected NumberFragment run(SpellContext ctx, PlayerEntity entity) throws BlunderException {
        return new NumberFragment(entity.getHungerManager().getSaturationLevel());
    }
}

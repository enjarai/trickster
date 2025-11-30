package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.RollableFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NoSuchFleckBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class RollFleckTrick extends AbstractFleckTrick<RollFleckTrick> {
    public RollFleckTrick() {
        super(
                Pattern.of(0, 6, 4, 2, 8, 7, 6, 3, 0, 1, 2, 5, 8),
                Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), RollFleckTrick::rollFleck, FragmentType.NUMBER)
        );
    }

    public NumberFragment rollFleck(SpellContext ctx, NumberFragment id, NumberFragment roll, Optional<List<EntityFragment>> targets) {
        var players = getPlayersInRangeOrTargets(ctx, targets);
        players.forEach(player -> {
            var flecks = player.getComponent(ModEntityComponents.FLECKS).getFlecks();
            if (!flecks.containsKey(id.asInt())) {
                throw new NoSuchFleckBlunder(this);
            }
            var fleck = flecks.get(id.asInt()).fleck();
            if (fleck instanceof RollableFleck rfleck) {
                display(ctx, id, rfleck.rollFleck((float) (roll.number())), Optional.of(List.of(EntityFragment.from(player))));
            }
        });

        return id;
    }
}

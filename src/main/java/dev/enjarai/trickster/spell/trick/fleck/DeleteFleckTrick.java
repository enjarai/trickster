package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class DeleteFleckTrick extends AbstractFleckTrick<DeleteFleckTrick> {
    public DeleteFleckTrick() {
        super(Pattern.of(7, 0, 3, 6, 7, 8, 5, 2, 7),
                Signature.of(FragmentType.NUMBER, FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), DeleteFleckTrick::deleteFleck, FragmentType.NUMBER));
    }

    public NumberFragment deleteFleck(SpellContext ctx, NumberFragment id, Optional<List<EntityFragment>> targets) throws BlunderException {
        var players = getPlayersInRangeOrTargets(ctx, targets);
        players.forEach(player -> player.getComponent(ModEntityComponents.FLECKS).removeFleck(id.asInt()));

        return id;
    }
}

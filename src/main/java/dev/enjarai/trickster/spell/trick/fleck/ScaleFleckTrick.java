package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.LineFleck;
import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NoSuchFleckBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Optional;

public class ScaleFleckTrick extends AbstractFleckTrick<ScaleFleckTrick> {
    public ScaleFleckTrick() {
        super(
                /*
                6 7 8
                3 4 5
                0 1 2
                 */
                Pattern.of(6, 3, 4, 5, 2, 1, 0, 6, 7, 8, 2),
                Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), ScaleFleckTrick::scaleFleck, FragmentType.NUMBER)
        );
    }

    public NumberFragment scaleFleck(SpellContext ctx, NumberFragment id, NumberFragment size, Optional<List<EntityFragment>> targets) {
        var players = getPlayersInRangeOrTargets(ctx, targets);
        players.forEach(player -> {
            var flecks = ((PlayerEntity) player).getComponent(ModEntityComponents.FLECKS).getFlecks();
            if (!flecks.containsKey(id.asInt())) {
                throw new NoSuchFleckBlunder(this);
            }
            var fleck = flecks.get(id.asInt()).fleck();
            if (fleck instanceof SpellFleck sfleck) {
                display(ctx, id, new SpellFleck(
                        sfleck.pos(),
                        sfleck.facing(),
                        sfleck.spell(),
                        (float) size.number(),
                        sfleck.roll()
                ), Optional.of(List.of(EntityFragment.from(player))));
            } else if (fleck instanceof LineFleck lfleck) {
                display(ctx, id, new LineFleck(
                        lfleck.pos(),
                        lfleck.pos2(),
                        (float) size.number()
                ), Optional.of(List.of(EntityFragment.from(player))));
            }
        });

        return id;
    }
}

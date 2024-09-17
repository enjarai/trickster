package dev.enjarai.trickster.spell.mana.handler;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.ManaHandler;
import dev.enjarai.trickster.spell.mana.ManaHandlerType;
import dev.enjarai.trickster.spell.mana.ManaLink;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import io.wispforest.accessories.utils.EndecUtils;
import io.wispforest.endec.StructEndec;

import java.util.List;

public class DefaultManaHandler implements ManaHandler {
    public static final StructEndec<DefaultManaHandler> ENDEC = EndecUtils.structUnit(DefaultManaHandler::new);

    @Override
    public ManaHandlerType<?> type() {
        return ManaHandlerType.DEFAULT;
    }

    @Override
    public void handleUseMana(Trick trickSource, SpellContext ctx, ManaPool pool, List<ManaLink> manaLinks, float amount) throws BlunderException {
        var links = manaLinks.stream().filter(link -> link.getAvailable() > 0).toList();

        if (!links.isEmpty()) {
            float totalAvailable = 0;
            float leftOver = 0;

            for (var link : links) {
                totalAvailable += link.getAvailable();
            }

            for (var link : links) {
                float available = link.getAvailable();

                float ratio = available / totalAvailable;
                float ratioD = amount * ratio;
                float used = link.useMana(trickSource, ctx.source(), pool, ratioD);

                if (used < ratioD) {
                    leftOver += ratioD - used;
                }
            }

            amount = leftOver;
        }

        if (!pool.decrease(amount)) {
            throw new NotEnoughManaBlunder(trickSource, amount);
        }
    }
}

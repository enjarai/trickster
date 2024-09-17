package dev.enjarai.trickster.spell.mana.handler;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.ManaHandler;
import dev.enjarai.trickster.spell.mana.ManaHandlerType;
import dev.enjarai.trickster.spell.mana.ManaLink;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.trick.blunder.NotEnoughManaBlunder;
import io.wispforest.accessories.utils.EndecUtils;
import io.wispforest.endec.StructEndec;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Items;

import java.util.List;

public class AmethystManaHandler implements ManaHandler {
    public static final StructEndec<AmethystManaHandler> ENDEC = EndecUtils.structUnit(AmethystManaHandler::new);

    @Override
    public ManaHandlerType<?> type() {
        return ManaHandlerType.AMETHYST;
    }

    @Override
    public void handleUseMana(Trick trickSource, SpellContext ctx, ManaPool pool, List<ManaLink> manaLinks, float amount) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(trickSource));

        if (pool.get() >= amount) {
            pool.decrease(amount);
            return;
        } else {
            amount -= pool.get();
            pool.set(0);
        }

        var neededShards = amount / pool.getMax();
        var consumedShards = Inventories.remove(player.getInventory(), s -> s.isOf(Items.AMETHYST_SHARD), (int) Math.ceil(neededShards), false);
        var shardMana = consumedShards * pool.getMax();

        if (shardMana >= amount) {
            pool.set(shardMana - amount);
        } else {
            throw new NotEnoughManaBlunder(trickSource, amount); //TODO: wrong amount?
        }
    }
}

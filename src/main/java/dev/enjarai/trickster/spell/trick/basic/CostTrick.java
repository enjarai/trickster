package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingCostBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;

public class CostTrick extends Trick<CostTrick> {
    public CostTrick() {
        super(Pattern.of(1, 5, 8, 6, 3, 1), Signature.of(CostTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        if (!player.getInventory().contains(ModItems.SPELL_COST)) {
            throw new MissingCostBlunder(this);
        }

        player.getInventory().remove(i -> i.isIn(ModItems.SPELL_COST), 1, player.playerScreenHandler.getCraftingInput());
        return VoidFragment.INSTANCE;
    }
}

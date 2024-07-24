package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingCostBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class CostTrick extends Trick {
    public CostTrick() {
        super(Pattern.of(1, 5, 8, 6, 3, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        if (!player.getInventory().contains(ModItems.SPELL_COST)) {
            throw new MissingCostBlunder(this);
        }

        player.getInventory().remove(i -> i.isIn(ModItems.SPELL_COST), 1, player.playerScreenHandler.getCraftingInput());
        return VoidFragment.INSTANCE;
    }
}

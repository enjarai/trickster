package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.MissingCostBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class CostTrick extends Trick<CostTrick> {
    public CostTrick() {
        super(Pattern.of(1, 5, 8, 6, 3, 1), Signature.of(CostTrick::run, FragmentType.VOID));
    }

    public VoidFragment run(SpellContext ctx) {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        ModCriteria.USE_COST_PLOY.trigger(player);

        if (!player.getInventory().contains(ModItems.SPELL_COST)) {
            throw new MissingCostBlunder(this);
        }

        player.getInventory().remove(i -> i.isIn(ModItems.SPELL_COST), 1, player.playerScreenHandler.getCraftingInput());
        return VoidFragment.INSTANCE;
    }
}

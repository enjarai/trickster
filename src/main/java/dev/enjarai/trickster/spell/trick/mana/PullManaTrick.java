package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import net.minecraft.item.ItemStack;

public class PullManaTrick extends AbstractConduitTrick {
    public PullManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    protected float affect(SpellContext ctx, ItemStack stack, float limit) {
        var comp = stack.get(ModComponents.MANA);

        if (comp == null)
            return 0;

        var self = ctx.source().getManaPool();
        var target = comp.pool().makeClone();
        var result = limit - target.use(limit);
        stack.set(ModComponents.MANA, comp.with(target));
        var leftover = self.refill(result);
        target = stack.get(ModComponents.MANA).pool().makeClone();
        target.refill(leftover);
        stack.set(ModComponents.MANA, comp.with(target));
        return result - leftover;
    }
}

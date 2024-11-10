package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import net.minecraft.item.ItemStack;

public class PushManaTrick extends AbstractConduitTrick {
    public PushManaTrick() {
        super(Pattern.of(7, 8, 4, 6, 7, 5, 4, 3, 7, 2, 1, 0, 7, 4, 1));
    }

    @Override
    protected float affect(SpellContext ctx, ItemStack stack, float limit) {
        var comp = stack.get(ModComponents.MANA);

        if (comp == null || !comp.rechargeable())
            return 0;

        var self = ctx.source().getManaPool();
        var target = comp.pool().makeClone();
        var result = limit - self.use(limit);
        var leftover = target.refill(result);
        stack.set(ModComponents.MANA, comp.with(target));
        self.refill(leftover);
        return result - leftover;
    }
}

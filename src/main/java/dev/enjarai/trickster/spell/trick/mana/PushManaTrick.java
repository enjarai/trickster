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

        if (comp == null)
            return 0;

        var world = ctx.source().getWorld();
        var self = ctx.source().getManaPool();
        var result = limit - self.use(limit, world);
        comp = stack.get(ModComponents.MANA);
        var target = comp.pool().makeClone(world);
        var leftover = target.refill(result, world);
        stack.set(ModComponents.MANA, comp.with(target));
        self.refill(leftover, world);
        return result - leftover;
    }
}

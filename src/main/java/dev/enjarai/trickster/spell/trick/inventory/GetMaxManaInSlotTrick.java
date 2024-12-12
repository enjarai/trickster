package dev.enjarai.trickster.spell.trick.inventory;

import java.util.List;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class GetMaxManaInSlotTrick extends Trick {
    public GetMaxManaInSlotTrick() {
        super(Pattern.of(0, 2, 3, 0, 4, 3, 6, 8, 5, 4, 2, 1, 5, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        float result = 0;

        for (var slot : expectVariadic(fragments, 0, SlotFragment.class)) {
            var stack = slot.reference(this, ctx);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null)
                continue;

            result += comp.pool().getMax(ctx.source().getWorld());
        }

        return new NumberFragment(result);
    }
}

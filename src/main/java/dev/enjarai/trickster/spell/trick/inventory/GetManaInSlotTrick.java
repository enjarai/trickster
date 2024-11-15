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

public class GetManaInSlotTrick extends Trick {
    public GetManaInSlotTrick() {
        super(Pattern.of(3, 4, 5, 2, 4, 0, 3, 6, 8, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        float result = 0;

        for (var slot : expectVariadic(fragments, 1, SlotFragment.class)) {
            var stack = slot.reference(this, ctx);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null)
                continue;

            result += comp.pool().get();
        }

        return new NumberFragment(result);
    }
}

package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class GetMaxManaInSlotTrick extends Trick<GetMaxManaInSlotTrick> {
    public GetMaxManaInSlotTrick() {
        super(Pattern.of(0, 2, 3, 0, 4, 3, 6, 8, 5, 4, 2, 1, 5, 2), Signature.of(variadic(FragmentType.SLOT).unpack(), GetMaxManaInSlotTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, List<SlotFragment> slots) throws BlunderException {
        float result = 0;

        for (var slot : slots) {
            var stack = slot.reference(this, ctx);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null) {
                continue;
            }

            result += comp.pool().getMax(ctx.source().getWorld());
        }

        return new NumberFragment(result);
    }
}

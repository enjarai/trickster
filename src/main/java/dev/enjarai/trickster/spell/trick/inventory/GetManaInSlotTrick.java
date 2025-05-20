package dev.enjarai.trickster.spell.trick.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetManaInSlotTrick extends Trick<GetManaInSlotTrick> {
    public GetManaInSlotTrick() {
        super(Pattern.of(3, 4, 5, 2, 4, 0, 3, 6, 8, 5), Signature.of(variadic(FragmentType.SLOT).unpack(), GetManaInSlotTrick::run));
    }

    public Fragment run(SpellContext ctx, List<SlotFragment> slots) throws BlunderException {
        float result = 0;
        var identifiedKnots = new ArrayList<UUID>();

        for (var slot : slots) {
            var stack = slot.reference(this, ctx);
            var comp = stack.get(ModComponents.MANA);

            if (comp == null) {
                continue;
            }

            if (comp.pool() instanceof SharedManaPool(UUID uuid)) {
                if (identifiedKnots.contains(uuid)) {
                    continue;
                }
                identifiedKnots.add(uuid);
            }
            result += comp.pool().get(ctx.source().getWorld());
        }

        return new NumberFragment(result);
    }
}

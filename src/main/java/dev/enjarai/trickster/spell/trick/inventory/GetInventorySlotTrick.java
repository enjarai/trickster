package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class GetInventorySlotTrick extends Trick {
    public GetInventorySlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.NUMBER, 0).number();
        var source = supposeEitherInput(fragments, FragmentType.VECTOR, FragmentType.ENTITY, 1)
            .map(either -> either
                    .mapLeft(VectorFragment::toBlockPos)
                    .mapRight(EntityFragment::uuid));

        return new SlotFragment((int) Math.round(slot), source);
    }
}

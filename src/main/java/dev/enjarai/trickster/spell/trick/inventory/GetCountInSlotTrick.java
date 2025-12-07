package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetCountInSlotTrick extends Trick<GetCountInSlotTrick> {
    public GetCountInSlotTrick() {
        super(Pattern.of(7, 8, 6, 7, 4, 3, 6, 0, 3, 1, 2, 5, 7), Signature.of(FragmentType.SLOT, GetCountInSlotTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, SlotFragment slot) {
        return new NumberFragment(slot.getAmount(this, ctx));
    }
}

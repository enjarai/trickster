package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class GetCountInSlotTrick extends Trick<GetCountInSlotTrick> {
    public GetCountInSlotTrick() {
        super(Pattern.of(7, 8, 6, 7, 4, 3, 6, 0, 3, 1, 2, 5, 7), Signature.of(FragmentType.SLOT, GetCountInSlotTrick::run));
    }

    public Fragment run(SpellContext ctx, SlotFragment slot) throws BlunderException {
        return new NumberFragment(slot.reference(this, ctx).getCount());
    }
}

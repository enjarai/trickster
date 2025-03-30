package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GetItemInSlotTrick extends Trick<GetItemInSlotTrick> {
    public GetItemInSlotTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 2, 4, 6, 3, 4), Signature.of(FragmentType.SLOT, GetItemInSlotTrick::run));
    }

    public Fragment run(SpellContext ctx, SlotFragment slot) throws BlunderException {
        return new ItemTypeFragment(slot.getItem(this, ctx));
    }
}


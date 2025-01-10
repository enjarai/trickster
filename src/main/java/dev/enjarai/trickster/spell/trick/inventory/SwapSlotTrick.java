package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class SwapSlotTrick extends Trick<SwapSlotTrick> {
    public SwapSlotTrick() {
        super(Pattern.of(1, 4, 7, 6, 4, 2, 1, 0, 4, 8, 7), Signature.of(FragmentType.SLOT, FragmentType.SLOT, SwapSlotTrick::run));
    }

    public Fragment run(SpellContext ctx, SlotFragment slot, SlotFragment slot2) throws BlunderException {
        slot.swapWith(this, ctx, slot2);
        return VoidFragment.INSTANCE;
    }
}

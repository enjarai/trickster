package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetItemInSlotTrick extends Trick<GetItemInSlotTrick> {
    public GetItemInSlotTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 2, 4, 6, 3, 4), Signature.of(FragmentType.SLOT, GetItemInSlotTrick::run, FragmentType.ITEM_TYPE));
    }

    public ItemTypeFragment run(SpellContext ctx, SlotFragment slot) {
        return new ItemTypeFragment(slot.getItem(this, ctx));
    }
}

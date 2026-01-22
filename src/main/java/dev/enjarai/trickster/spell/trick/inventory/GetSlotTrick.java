package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetSlotTrick extends Trick<GetSlotTrick> {
    public GetSlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7), Signature.of(FragmentType.CONTAINER, FragmentType.NUMBER, GetSlotTrick::fromContainer, FragmentType.SLOT));
    }

    public SlotFragment fromContainer(SpellContext ctx, ContainerFragment container, NumberFragment slot) {
        return new SlotFragment(new StorageSource.Slot(slot.asInt(), container.source()), container.variantType());
    }
}

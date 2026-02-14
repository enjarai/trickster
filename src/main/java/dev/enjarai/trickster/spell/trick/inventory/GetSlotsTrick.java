package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetSlotsTrick<V, F extends ResourceVariantFragment<V> & Fragment> extends Trick<GetSlotsTrick<V, F>> {
    public GetSlotsTrick() {
        super(Pattern.of(7, 4, 3, 0, 2, 5, 4, 0, 7, 2, 4),
                Signature.of(FragmentType.CONTAINER, ArgType.simple(ResourceVariantFragment.class).variadicOfArg().unpack(), GetSlotsTrick::fromContainer, FragmentType.SLOT.listOfRet()));
    }

    private List<SlotFragment> fromSlots(SpellContext ctx, List<SlotFragment> slots, List<ResourceVariantFragment> itemTypes) {
        if (!itemTypes.isEmpty()) {
            return slots.stream()
                    .filter(slot -> itemTypes.stream().anyMatch(f -> f.slotContains(this, ctx, slot)))
                    .toList();
        } else {
            return slots;
        }
    }

    public List<SlotFragment> fromContainer(SpellContext ctx, ContainerFragment container, List<ResourceVariantFragment> itemTypes) {
        itemTypes.forEach(f -> f.assertVariantType(this, container.variantType()));
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, container.source(), container.variantType()), itemTypes);
    }
}

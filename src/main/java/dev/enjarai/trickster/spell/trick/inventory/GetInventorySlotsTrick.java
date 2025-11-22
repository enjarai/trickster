package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;

public class GetInventorySlotsTrick extends Trick<GetInventorySlotsTrick> {
    public GetInventorySlotsTrick() {
        super(Pattern.of(7, 4, 3, 0, 2, 5, 4, 0, 7, 2, 4), Signature.of(FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromCaster, FragmentType.SLOT.listOfRet()));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromVector, FragmentType.SLOT.listOfRet()));
        overload(Signature.of(FragmentType.ENTITY, FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromEntity, FragmentType.SLOT.listOfRet()));
    }

    private List<SlotFragment> fromSlots(SpellContext ctx, List<SlotFragment> slots, List<ItemTypeFragment> itemTypes) {
        var itemTypesFilter = itemTypes.stream().map(typeFragment -> {
            return typeFragment.item();
        }).toList();

        if (itemTypes.size() > 0) {
            return slots.stream()
                    .filter(slot -> itemTypesFilter.contains(slot.getItem(this, ctx)))
                    .toList();
        } else {
            return slots;
        }
    }

    public List<SlotFragment> fromCaster(SpellContext ctx, List<ItemTypeFragment> itemTypes) {
        var slots = SlotFragment.getSlots(this, ctx, Optional.empty());
        return fromSlots(ctx, slots, itemTypes);
    }

    public List<SlotFragment> fromVector(SpellContext ctx, VectorFragment pos, List<ItemTypeFragment> itemTypes) {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, Optional.of(Either.left(pos.toBlockPos()))), itemTypes);
    }

    public List<SlotFragment> fromEntity(SpellContext ctx, EntityFragment entity, List<ItemTypeFragment> itemTypes) {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid()))), itemTypes);
    }
}

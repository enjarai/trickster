package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;

public class GetInventorySlotsTrick extends Trick<GetInventorySlotsTrick> {
    public GetInventorySlotsTrick() {
        super(Pattern.of(7, 4, 3, 0, 2, 5, 4, 0, 7, 2, 4), Signature.of(FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromCaster, FragmentType.LIST));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromVector, FragmentType.LIST));
        overload(Signature.of(FragmentType.ENTITY, FragmentType.ITEM_TYPE.variadicOfArg().unpack(), GetInventorySlotsTrick::fromEntity, FragmentType.LIST));
    }

    private ListFragment fromSlots(SpellContext ctx, ListFragment slots, List<ItemTypeFragment> itemTypes) {
        var itemTypesFilter = itemTypes.stream().map(typeFragment -> {
            return typeFragment.item();
        }).toList();

        if (itemTypes.size() > 0) {
            return new ListFragment(slots.fragments().stream().filter(slot -> (slot instanceof SlotFragment slotFragment) && itemTypesFilter.contains(slotFragment.getItem(this, ctx))
            ).toList());
        } else {
            return slots;
        }
    }

    public ListFragment fromCaster(SpellContext ctx, List<ItemTypeFragment> itemTypes) throws BlunderException {
        var slots = SlotFragment.getSlots(this, ctx, Optional.empty());
        return fromSlots(ctx, slots, itemTypes);
    }

    public ListFragment fromVector(SpellContext ctx, VectorFragment pos, List<ItemTypeFragment> itemTypes) throws BlunderException {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, Optional.of(Either.left(pos.toBlockPos()))), itemTypes);
    }

    public ListFragment fromEntity(SpellContext ctx, EntityFragment entity, List<ItemTypeFragment> itemTypes) throws BlunderException {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid()))), itemTypes);
    }
}

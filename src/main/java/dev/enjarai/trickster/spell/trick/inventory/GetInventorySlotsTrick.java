package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.*;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class GetInventorySlotsTrick<V, F extends ResourceVariantFragment<V> & Fragment> extends Trick<GetInventorySlotsTrick<V, F>> {
    private final VariantType<V> variantType;

    public GetInventorySlotsTrick(VariantType<V> variantType, FragmentType<F> matchableFragmentType) {
        super(Pattern.of(7, 4, 3, 0, 2, 5, 4, 0, 7, 2, 4), Signature.of(matchableFragmentType.variadicOfArg().unpack(), GetInventorySlotsTrick::fromCaster, FragmentType.SLOT.listOfRet()));
        this.variantType = variantType;
        overload(Signature.of(FragmentType.VECTOR, matchableFragmentType.variadicOfArg().unpack(), GetInventorySlotsTrick::fromVector, FragmentType.SLOT.listOfRet()));
        overload(Signature.of(FragmentType.ENTITY, matchableFragmentType.variadicOfArg().unpack(), GetInventorySlotsTrick::fromEntity, FragmentType.SLOT.listOfRet()));
    }

    private List<SlotFragment> fromSlots(SpellContext ctx, List<SlotFragment> slots, List<F> itemTypes) {
        if (!itemTypes.isEmpty()) {
            return slots.stream()
                    .filter(slot -> itemTypes.stream().anyMatch(f -> f.slotContains(this, ctx, slot)))
                    .toList();
        } else {
            return slots;
        }
    }

    public List<SlotFragment> fromCaster(SpellContext ctx, List<F> itemTypes) {
        var slots = SlotFragment.getSlots(this, ctx, StorageSource.Caster.INSTANCE, variantType);
        return fromSlots(ctx, slots, itemTypes);
    }

    public List<SlotFragment> fromVector(SpellContext ctx, VectorFragment pos, List<F> itemTypes) {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, new StorageSource.Block(pos.toBlockPos()), variantType), itemTypes);
    }

    public List<SlotFragment> fromEntity(SpellContext ctx, EntityFragment entity, List<F> itemTypes) {
        return fromSlots(ctx, SlotFragment.getSlots(this, ctx, new StorageSource.Entity(
                entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid()), variantType), itemTypes);
    }
}

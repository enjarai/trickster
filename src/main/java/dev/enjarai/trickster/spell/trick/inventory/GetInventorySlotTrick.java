package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetInventorySlotTrick extends Trick<GetInventorySlotTrick> {
    private final VariantType<?> variantType;

    public GetInventorySlotTrick(VariantType<?> variantType) {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7), Signature.of(FragmentType.NUMBER, GetInventorySlotTrick::fromCaster, FragmentType.SLOT));
        this.variantType = variantType;
        overload(Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, GetInventorySlotTrick::fromVector, FragmentType.SLOT));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.ENTITY, GetInventorySlotTrick::fromEntity, FragmentType.SLOT));
    }

    public SlotFragment fromCaster(SpellContext ctx, NumberFragment slot) {
        return new SlotFragment(new StorageSource.Slot(slot.asInt(), StorageSource.Caster.INSTANCE), variantType);
    }

    public SlotFragment fromVector(SpellContext ctx, NumberFragment slot, VectorFragment pos) {
        return new SlotFragment(new StorageSource.Slot(slot.asInt(), new StorageSource.Block(pos.toBlockPos())), variantType);
    }

    public SlotFragment fromEntity(SpellContext ctx, NumberFragment slot, EntityFragment entity) {
        return new SlotFragment(new StorageSource.Slot(slot.asInt(), new StorageSource.Entity(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid())), variantType);
    }
}

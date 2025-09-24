package dev.enjarai.trickster.spell.trick.inventory;

import com.mojang.datafixers.util.Either;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class GetInventorySlotTrick extends Trick<GetInventorySlotTrick> {
    public GetInventorySlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7), Signature.of(FragmentType.NUMBER, GetInventorySlotTrick::fromCaster, FragmentType.SLOT));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, GetInventorySlotTrick::fromVector, FragmentType.SLOT));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.ENTITY, GetInventorySlotTrick::fromEntity, FragmentType.SLOT));
    }

    public SlotFragment fromCaster(SpellContext ctx, NumberFragment slot) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.empty());
    }

    public SlotFragment fromVector(SpellContext ctx, NumberFragment slot, VectorFragment pos) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.of(Either.left(pos.toBlockPos())));
    }

    public SlotFragment fromEntity(SpellContext ctx, NumberFragment slot, EntityFragment entity) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid())));
    }
}

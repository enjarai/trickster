package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

import com.mojang.datafixers.util.Either;

public class GetInventorySlotTrick extends Trick<GetInventorySlotTrick> {
    public GetInventorySlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7), Signature.of(FragmentType.NUMBER, GetInventorySlotTrick::fromCaster));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.VECTOR, GetInventorySlotTrick::fromVector));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.ENTITY, GetInventorySlotTrick::fromEntity));
    }

    public Fragment fromCaster(SpellContext ctx, NumberFragment slot) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.empty());
    }

    public Fragment fromVector(SpellContext ctx, NumberFragment slot, VectorFragment pos) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.of(Either.left(pos.toBlockPos())));
    }

    public Fragment fromEntity(SpellContext ctx, NumberFragment slot, EntityFragment entity) throws BlunderException {
        return new SlotFragment(slot.asInt(), Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid())));
    }
}

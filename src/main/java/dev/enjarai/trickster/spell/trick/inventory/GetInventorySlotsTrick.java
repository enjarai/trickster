package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

import com.mojang.datafixers.util.Either;

public class GetInventorySlotsTrick extends Trick<GetInventorySlotsTrick> {
    public GetInventorySlotsTrick() {
        super(Pattern.of(5, 8), Signature.of(GetInventorySlotsTrick::fromCaster));
        overload(Signature.of(FragmentType.VECTOR, GetInventorySlotsTrick::fromVector));
        overload(Signature.of(FragmentType.ENTITY, GetInventorySlotsTrick::fromEntity));
    }

    public Fragment fromCaster(SpellContext ctx) throws BlunderException {
        return SlotFragment.getSlots(this, ctx, Optional.empty());
    }

    public Fragment fromVector(SpellContext ctx, VectorFragment pos) throws BlunderException {
        return SlotFragment.getSlots(this, ctx, Optional.of(Either.left(pos.toBlockPos())));
    }

    public Fragment fromEntity(SpellContext ctx, EntityFragment entity) throws BlunderException {
        return SlotFragment.getSlots(this, ctx, Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid())));
    }
}

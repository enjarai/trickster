package dev.enjarai.trickster.spell.trick.inventory;

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

public class GetInventorySizeTrick extends Trick<GetInventorySizeTrick> {
    public GetInventorySizeTrick() {
        super(Pattern.of(7, 6, 3, 4, 5, 8, 7, 3, 0, 2, 5, 7), Signature.of(GetInventorySizeTrick::fromCaster, FragmentType.NUMBER));
        overload(Signature.of(FragmentType.VECTOR, GetInventorySizeTrick::fromVector, FragmentType.NUMBER));
        overload(Signature.of(FragmentType.ENTITY, GetInventorySizeTrick::fromEntity, FragmentType.NUMBER));
    }

    public NumberFragment fromCaster(SpellContext ctx) throws BlunderException {
        return SlotFragment.getInventoryLength(this, ctx, Optional.empty());
    }

    public NumberFragment fromVector(SpellContext ctx, VectorFragment pos) throws BlunderException {
        return SlotFragment.getInventoryLength(this, ctx, Optional.of(Either.left(pos.toBlockPos())));
    }

    public NumberFragment fromEntity(SpellContext ctx, EntityFragment entity) throws BlunderException {
        return SlotFragment.getInventoryLength(this, ctx, Optional.of(Either.right(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid())));
    }
}

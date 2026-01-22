package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetInventorySizeTrick extends Trick<GetInventorySizeTrick> {
    private final VariantType<?> variantType;

    public GetInventorySizeTrick(VariantType<?> variantType) {
        super(Pattern.of(7, 6, 3, 4, 5, 8, 7, 3, 0, 2, 5, 7), Signature.of(FragmentType.CONTAINER, GetInventorySizeTrick::fromContainer, FragmentType.NUMBER));
        this.variantType = variantType;
        //        overload(Signature.of(FragmentType.VECTOR, GetInventorySizeTrick::fromVector, FragmentType.NUMBER));
        //        overload(Signature.of(FragmentType.ENTITY, GetInventorySizeTrick::fromEntity, FragmentType.NUMBER));
        //        overload(Signature.of(GetInventorySizeTrick::fromCaster, FragmentType.NUMBER));
    }

    public NumberFragment fromCaster(SpellContext ctx) {
        return StorageSource.Caster.INSTANCE.getInventoryLength(this, ctx, variantType);
    }

    public NumberFragment fromVector(SpellContext ctx, VectorFragment pos) {
        return new StorageSource.Block(pos.toBlockPos()).getInventoryLength(this, ctx, variantType);
    }

    public NumberFragment fromEntity(SpellContext ctx, EntityFragment entity) {
        return new StorageSource.Entity(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid()).getInventoryLength(this, ctx, variantType);
    }

    public NumberFragment fromContainer(SpellContext ctx, ContainerFragment container) {
        return container.source().getInventoryLength(this, ctx, variantType);
    }
}

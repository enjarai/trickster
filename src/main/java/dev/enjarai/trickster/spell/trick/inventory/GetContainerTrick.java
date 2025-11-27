package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetContainerTrick extends Trick<GetContainerTrick> {
    private final VariantType<?> variantType;

    public GetContainerTrick(Pattern pattern, VariantType<?> variantType) {
        super(pattern, Signature.of(GetContainerTrick::fromCaster, FragmentType.CONTAINER));
        this.variantType = variantType;
        overload(Signature.of(FragmentType.VECTOR, GetContainerTrick::fromVector, FragmentType.CONTAINER));
        overload(Signature.of(FragmentType.ENTITY, GetContainerTrick::fromEntity, FragmentType.CONTAINER));
    }

    public ContainerFragment fromCaster(SpellContext ctx) {
        return new ContainerFragment(StorageSource.Caster.INSTANCE, variantType);
    }

    public ContainerFragment fromVector(SpellContext ctx, VectorFragment pos) {
        return new ContainerFragment(new StorageSource.Block(pos.toBlockPos()), variantType);
    }

    public ContainerFragment fromEntity(SpellContext ctx, EntityFragment entity) {
        return new ContainerFragment(new StorageSource.Entity(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getUuid()), variantType);
    }
}

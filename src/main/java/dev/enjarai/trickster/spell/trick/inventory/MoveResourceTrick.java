package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public class MoveResourceTrick extends Trick<MoveResourceTrick> {
    public MoveResourceTrick() {
        super(Pattern.of(7, 4, 6, 7, 8, 4, 0, 2, 4), Signature.of(FragmentType.SLOT, FragmentType.SLOT, FragmentType.NUMBER.optionalOfArg(), ArgType.simple(ResourceVariantFragment.class).variadicOfArg().unpack(), MoveResourceTrick::move, FragmentType.NUMBER));
        overload(Signature.of(FragmentType.CONTAINER, FragmentType.CONTAINER, FragmentType.NUMBER.optionalOfArg(), ArgType.simple(ResourceVariantFragment.class).variadicOfArg().unpack(), MoveResourceTrick::move, FragmentType.NUMBER));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> NumberFragment move(SpellContext ctx, StorageFragment sourceSlot, StorageFragment destinationSlot, Optional<NumberFragment> amount, List<ResourceVariantFragment> resourceTypes) throws BlunderException {
        if (sourceSlot.equals(destinationSlot)) {
            return new NumberFragment(0);
        }

        VariantType<T> variantType = (VariantType<T>) sourceSlot.variantType();
        resourceTypes.forEach(t -> t.assertVariantType(this, variantType));
        // Cursed but guaranteed to be correct
        var allowedResources = (List<ResourceVariantFragment<T>>) (List) resourceTypes;

        try (var trans = Transaction.openOuter()) {
            var moved = StorageUtil.move(
                sourceSlot.getStorage(this, ctx, variantType),
                destinationSlot.getStorage(this, ctx, variantType),
                v -> {
                    if (allowedResources.isEmpty()) return true;

                    for (var resource : allowedResources) {
                        if (resource.resourceMatches(this, ctx, v)) {
                            return true;
                        }
                    }
                    return false;
                },
                amount.map(NumberFragment::asLong).orElse(Long.MAX_VALUE),
                trans
            );

            ctx.useMana(this, destinationSlot.getMoveCost(this, ctx, sourceSlot.getSourceOrCasterPos(this, ctx), moved));

            trans.commit();
            return new NumberFragment(moved);
        }
    }
}

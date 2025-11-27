package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class MoveResourceTrick extends Trick<MoveResourceTrick> {
    public MoveResourceTrick() {
        super(Pattern.of(7, 4, 6, 7, 8, 4, 0, 2, 4), Signature.of(FragmentType.SLOT, FragmentType.SLOT, FragmentType.NUMBER.optionalOfArg(), MoveResourceTrick::move, FragmentType.NUMBER));
        overload(Signature.of(FragmentType.CONTAINER, FragmentType.CONTAINER, FragmentType.NUMBER.optionalOfArg(), MoveResourceTrick::move, FragmentType.NUMBER));
    }

    public <T> NumberFragment move(SpellContext ctx, SlotFragment sourceSlot, SlotFragment destinationSlot, Optional<NumberFragment> amount) {
        if (sourceSlot.equals(destinationSlot)) {
            return new NumberFragment(0);
        }

        VariantType<T> variantType = (VariantType<T>) sourceSlot.variantType();

        try (var trans = Transaction.openOuter()) {
            var moved = StorageUtil.move(
                    sourceSlot.getStorage(this, ctx, variantType),
                    destinationSlot.getStorage(this, ctx, variantType),
                    v -> true,
                    amount.map(NumberFragment::asLong).orElse(Long.MAX_VALUE),
                    trans
            );

            ctx.useMana(this, destinationSlot.getMoveCost(this, ctx, sourceSlot.getSourceOrCasterPos(this, ctx), moved));

            trans.commit();
            return new NumberFragment(moved);
        }
    }

    public <T> NumberFragment move(SpellContext ctx, ContainerFragment source, ContainerFragment destination, Optional<NumberFragment> amount) {
        if (source.equals(destination)) {
            return new NumberFragment(0);
        }

        VariantType<T> variantType = (VariantType<T>) source.variantType();

        try (var trans = Transaction.openOuter()) {
            var moved = StorageUtil.move(
                    source.getStorage(this, ctx, variantType),
                    destination.getStorage(this, ctx, variantType),
                    v -> true,
                    amount.map(NumberFragment::asLong).orElse(Long.MAX_VALUE),
                    trans
            );

            ctx.useMana(this, destination.getMoveCost(this, ctx, source.getSourceOrCasterPos(this, ctx), moved));

            trans.commit();
            return new NumberFragment(moved);
        }
    }
}

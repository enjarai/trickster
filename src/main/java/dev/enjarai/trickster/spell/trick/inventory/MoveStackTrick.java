package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.Optional;

public class MoveStackTrick extends Trick<MoveStackTrick> {
    public MoveStackTrick() {
        super(Pattern.of(7, 4, 6, 7, 8, 4, 0, 2, 4), Signature.of(FragmentType.SLOT, FragmentType.SLOT, FragmentType.NUMBER.optionalOfArg(), MoveStackTrick::move, FragmentType.NUMBER));
    }

    public NumberFragment move(SpellContext ctx, SlotFragment sourceSlot, SlotFragment destinationSlot, Optional<NumberFragment> amount) {
        if (sourceSlot.equals(destinationSlot)) {
            return new NumberFragment(0);
        }

        try (var trans = Transaction.openOuter()) {
            var moved = StorageUtil.move(
                    sourceSlot.slot().getSelfSlot(this, ctx, VariantType.ITEM),
                    destinationSlot.slot().getSelfSlot(this, ctx, VariantType.ITEM),
                    v -> true,
                    amount.map(NumberFragment::asLong).orElse(Long.MAX_VALUE),
                    trans
            );

            ctx.useMana(this, destinationSlot.getMoveCost(this, ctx, sourceSlot.getSourceOrCasterPos(this, ctx), moved));

            trans.commit();
            return new NumberFragment(moved);
        }
    }
}

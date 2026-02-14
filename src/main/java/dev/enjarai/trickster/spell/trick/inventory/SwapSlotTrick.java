package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class SwapSlotTrick extends Trick<SwapSlotTrick> {
    public SwapSlotTrick() {
        super(Pattern.of(1, 4, 7, 6, 4, 2, 1, 0, 4, 8, 7), Signature.of(FragmentType.SLOT, FragmentType.SLOT, SwapSlotTrick::run, FragmentType.VOID));
    }

    @SuppressWarnings("unchecked")
    public <T> VoidFragment run(SpellContext ctx, SlotFragment slot1, SlotFragment slot2) {
        VariantType<T> variantType = (VariantType<T>) slot1.variantType();

        try (var trans = Transaction.openOuter()) {
            var storage1 = slot1.getStorage(this, ctx, variantType);
            var storage2 = slot2.getStorage(this, ctx, variantType);

            var variant1 = storage1.getResource();
            var variant2 = storage2.getResource();

            var extracted1 = storage1.isResourceBlank() ? 0 : storage1.extract(variant1, storage2.getCapacity(), trans);
            var extracted2 = storage2.isResourceBlank() ? 0 : storage2.extract(variant2, storage1.getCapacity(), trans);

            var inserted1 = extracted1 == 0 ? 0 : storage2.insert(variant1, extracted1, trans);
            var inserted2 = extracted2 == 0 ? 0 : storage1.insert(variant2, extracted2, trans);

            if (inserted1 != extracted1 || inserted2 != extracted2) {
                throw new ItemInvalidBlunder(this);
            }

            trans.commit();
        }

        return VoidFragment.INSTANCE;
    }
}

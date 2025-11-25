package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.TransmogUtils;
import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class TransmogTrick extends Trick<TransmogTrick> {
    public TransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 1, 4, 7, 2), Signature.of(FragmentType.SLOT, FragmentType.ITEM_TYPE, TransmogTrick::transmog, FragmentType.BOOLEAN));
    }

    public BooleanFragment transmog(SpellContext ctx, SlotFragment slot, ItemTypeFragment item) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var currentTransmog = resource.getComponentMap().get(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        var targetItem = item.item();

        if (currentTransmog != null && currentTransmog.itemStack().getItem().equals(targetItem)) {
            return BooleanFragment.FALSE;
        }

        ctx.useMana(this, 20);
        slot.applyModifier(this, ctx, stack -> {
            TransmogUtils.transmogAppearanceOntoItemStack(targetItem.getDefaultStack(), stack);
            return stack;
        });

        return BooleanFragment.TRUE;
    }
}

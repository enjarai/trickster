package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.TransmogUtils;
import com.hidoni.transmog.registry.ModDataComponents;
import com.hidoni.transmog.registry.ModItems;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class HiddenTransmogTrick extends Trick<HiddenTransmogTrick> {
    public HiddenTransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 0, 1, 4, 7, 8, 2), Signature.of(FragmentType.SLOT, HiddenTransmogTrick::transmog, FragmentType.BOOLEAN));
    }

    public BooleanFragment transmog(SpellContext ctx, SlotFragment slot) {
        var stack = slot.reference(this, ctx);
        var currentTransmog = stack.get(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        if (currentTransmog != null && currentTransmog.itemStack().getItem().equals(ModItems.VOID_FRAGMENT.get())) {
            return BooleanFragment.FALSE;
        }

        ctx.useMana(this, 30);
        TransmogUtils.transmogAppearanceOntoItemStack(ModItems.VOID_FRAGMENT.get().getDefaultStack(), stack);

        return BooleanFragment.TRUE;
    }
}

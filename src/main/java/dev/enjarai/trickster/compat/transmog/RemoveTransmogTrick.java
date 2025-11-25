package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.TransmogUtils;
import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class RemoveTransmogTrick extends Trick<RemoveTransmogTrick> {
    public RemoveTransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 8, 4, 0, 2), Signature.of(FragmentType.SLOT, RemoveTransmogTrick::remove, FragmentType.BOOLEAN));
    }

    public BooleanFragment remove(SpellContext ctx, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);

        if (!TransmogUtils.isItemStackTransmogged(resource.toStack())) {
            return BooleanFragment.FALSE;
        }

        ctx.useMana(this, 10);
        slot.applyItemModifier(this, ctx, stack -> {
            stack.remove(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());
            return stack;
        });

        return BooleanFragment.TRUE;
    }
}

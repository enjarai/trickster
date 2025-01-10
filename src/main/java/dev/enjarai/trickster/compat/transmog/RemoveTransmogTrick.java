package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.TransmogUtils;
import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class RemoveTransmogTrick extends Trick<RemoveTransmogTrick> {
    public RemoveTransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 8, 4, 0, 2), Signature.of(FragmentType.SLOT, RemoveTransmogTrick::remove));
    }

    public Fragment remove(SpellContext ctx, SlotFragment slot) throws BlunderException {
        var stack = slot.reference(this, ctx);

        if (!TransmogUtils.isItemStackTransmogged(stack)) {
            return BooleanFragment.FALSE;
        }

        ctx.useMana(this, 10);
        stack.remove(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        return BooleanFragment.TRUE;
    }
}

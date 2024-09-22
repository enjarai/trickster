package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.TransmogUtils;
import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class TransmogTrick extends Trick {
    public TransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 1, 4, 7, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.SLOT, 0);
        var item = expectInput(fragments, FragmentType.ITEM_TYPE, 1);

        var stack = slot.reference(this, ctx);
        var currentTransmog = stack.get(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        var targetItem = item.item();

        if (currentTransmog != null && currentTransmog.itemStack().getItem().equals(targetItem)) {
            return BooleanFragment.FALSE;
        }

        ctx.useMana(this, 20);
        TransmogUtils.transmogAppearanceOntoItemStack(targetItem.getDefaultStack(), stack);

        return BooleanFragment.TRUE;
    }
}

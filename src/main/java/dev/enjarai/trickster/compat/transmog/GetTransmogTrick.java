package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class GetTransmogTrick extends Trick {
    public GetTransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 0, 1, 2, 8, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.SLOT, 0);

        var stack = slot.reference(this, ctx);
        var currentTransmog = stack.get(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        if (currentTransmog == null) {
            return VoidFragment.INSTANCE;
        }

        return new ItemTypeFragment(currentTransmog.itemStack().getItem());
    }
}

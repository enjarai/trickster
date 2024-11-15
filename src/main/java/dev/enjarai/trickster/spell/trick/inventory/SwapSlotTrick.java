package dev.enjarai.trickster.spell.trick.inventory;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class SwapSlotTrick extends Trick {
    public SwapSlotTrick() {
        super(Pattern.of(1, 4, 7, 6, 4, 2, 1, 0, 4, 8, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.SLOT, 0);
        var slot2 = expectInput(fragments, FragmentType.SLOT, 1);

        slot.swapWith(this, ctx, slot2);
        return VoidFragment.INSTANCE;
    }
}

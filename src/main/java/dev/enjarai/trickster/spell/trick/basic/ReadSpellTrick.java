package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;

import java.util.List;

public class ReadSpellTrick extends Trick {
    public ReadSpellTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5, 2, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = supposeInput(fragments, FragmentType.SLOT, 0)
                .or(() -> ctx.source().getOtherHandSlot())
                .orElseThrow(() -> new NoPlayerBlunder(this));
        var range = slot.getSourcePos(this, ctx).toCenterPos().subtract(ctx.source().getBlockPos().toCenterPos())
                .length();

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        return FragmentComponent.getFragment(slot.reference(this, ctx)).orElse(VoidFragment.INSTANCE);
    }
}

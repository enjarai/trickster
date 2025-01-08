package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class ClearSpellTrick extends Trick {
    public ClearSpellTrick() {
        super(Pattern.of(1, 4, 5, 8, 7, 6, 3, 4));
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

        slot.resetFragment(this, ctx);
        return VoidFragment.INSTANCE;
    }
}

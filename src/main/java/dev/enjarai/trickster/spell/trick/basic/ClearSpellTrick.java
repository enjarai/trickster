package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class ClearSpellTrick extends Trick<ClearSpellTrick> {
    public ClearSpellTrick() {
        super(Pattern.of(1, 4, 5, 8, 7, 6, 3, 4), Signature.of(FragmentType.SLOT.optionalOf(), ClearSpellTrick::run));
    }

    public Fragment run(SpellContext ctx, Optional<SlotFragment> optionalSlot) throws BlunderException {
        var slot = optionalSlot.or(() -> ctx.source().getOtherHandSlot()).orElseThrow(() -> new NoPlayerBlunder(this));
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        slot.resetFragment(this, ctx);
        return VoidFragment.INSTANCE;
    }
}

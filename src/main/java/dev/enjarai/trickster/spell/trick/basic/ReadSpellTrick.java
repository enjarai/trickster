package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;

import java.util.Optional;

public class ReadSpellTrick extends Trick<ReadSpellTrick> {
    public ReadSpellTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5, 2, 1), Signature.of(FragmentType.SLOT.optionalOf(), ReadSpellTrick::run, RetType.ANY.maybe()));
    }

    public Optional<Fragment> run(SpellContext ctx, Optional<SlotFragment> optionalSlot) throws BlunderException {
        var slot = optionalSlot.or(() -> ctx.source().getOtherHandSlot()).orElseThrow(() -> new NoPlayerBlunder(this));
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        return FragmentComponent.getFragment(slot.reference(this, ctx));
    }
}

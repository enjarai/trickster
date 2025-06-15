package dev.enjarai.trickster.spell.trick.func;

import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.blunder.NoSuchSpellSlotBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetSpellStateTrick extends Trick<GetSpellStateTrick> {
    public GetSpellStateTrick() {
        super(Pattern.of(3, 4, 5, 8, 6, 3),
                Signature.of(FragmentType.NUMBER.optionalOf(), GetSpellStateTrick::run));
    }

    public Fragment run(SpellContext ctx, Optional<NumberFragment> spellSlot) throws BlunderException {
        var manager = ctx.source().getExecutionManager().orElseThrow(() -> new IncompatibleSourceBlunder(this));
        return new NumberFragment(manager.getSpellState(spellSlot.map(slot -> slot.asInt()).orElse(ctx.data().getSlot().orElseThrow(() -> new IncompatibleSourceBlunder(this))))
                .orElseThrow(() -> new NoSuchSpellSlotBlunder(this)));
    }
}

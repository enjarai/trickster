package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;

public class OtherHandSlotTrick extends Trick<OtherHandSlotTrick> {
    public OtherHandSlotTrick() {
        super(Pattern.of(7, 4, 1, 2, 5, 4), Signature.of(OtherHandSlotTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        return ctx.source().getOtherHandSlot().orElseThrow(() -> new MissingItemBlunder(this));
    }
}

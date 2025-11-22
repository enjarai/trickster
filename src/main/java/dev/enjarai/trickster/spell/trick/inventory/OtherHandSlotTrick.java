package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class OtherHandSlotTrick extends Trick<OtherHandSlotTrick> {
    public OtherHandSlotTrick() {
        super(Pattern.of(7, 4, 1, 2, 5, 4), Signature.of(OtherHandSlotTrick::run, FragmentType.SLOT));
    }

    public SlotFragment run(SpellContext ctx) {
        return ctx.source().getOtherHandSlot().orElseThrow(() -> new MissingItemBlunder(this));
    }
}

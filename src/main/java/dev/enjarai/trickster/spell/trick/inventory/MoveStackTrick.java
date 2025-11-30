package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class MoveStackTrick extends Trick<MoveStackTrick> {
    public MoveStackTrick() {
        super(Pattern.of(7, 4, 6, 7, 8, 4, 0, 2, 4), Signature.of(FragmentType.SLOT, FragmentType.SLOT, FragmentType.NUMBER.optionalOfArg(), MoveStackTrick::move, FragmentType.NUMBER));
    }

    public NumberFragment move(SpellContext ctx, SlotFragment sourceSlot, SlotFragment destinationSlot, Optional<NumberFragment> amount) {
        return new NumberFragment(sourceSlot.moveInto(this, ctx, destinationSlot, amount.map(NumberFragment::asInt).orElse(Integer.MAX_VALUE)));
    }
}

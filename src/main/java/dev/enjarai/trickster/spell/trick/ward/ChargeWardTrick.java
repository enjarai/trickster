package dev.enjarai.trickster.spell.trick.ward;

import java.util.Optional;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.WardFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class ChargeWardTrick extends Trick<ChargeWardTrick> {
    public ChargeWardTrick() {
        //TODO: pattern
        super(Pattern.of(), Signature.of(FragmentType.WARD, FragmentType.NUMBER.optionalOfArg(), ChargeWardTrick::charge, FragmentType.WARD));
    }

    public WardFragment charge(SpellContext ctx, WardFragment ward, Optional<NumberFragment> max) {
        //TODO: actually impl this
        return ward;
    }
}

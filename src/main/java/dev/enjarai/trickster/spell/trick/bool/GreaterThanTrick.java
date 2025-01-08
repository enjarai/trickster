package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class GreaterThanTrick extends DistortionTrick<GreaterThanTrick> {
    public GreaterThanTrick() {
        super(Pattern.of(1, 5, 7), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, GreaterThanTrick::run));
    }

    public Fragment run(SpellContext ctx, NumberFragment left, NumberFragment right) throws BlunderException {
        return BooleanFragment.of(left.number() > right.number());
    }
}

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

public class LesserThanTrick extends DistortionTrick<LesserThanTrick> {
    public LesserThanTrick() {
        super(Pattern.of(1, 3, 7), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, LesserThanTrick::run));
    }

    public Fragment run(SpellContext ctx, NumberFragment left, NumberFragment right) throws BlunderException {
        return BooleanFragment.of(left.number() < right.number());
    }
}

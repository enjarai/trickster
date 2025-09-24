package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

public class LogTrick extends DistortionTrick<LogTrick> {
    public LogTrick() {
        super(Pattern.of(0, 7, 2), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, LogTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment logBase, NumberFragment number) throws BlunderException {
        return new NumberFragment(Math.log(number.number()) / Math.log(logBase.number()));
    }
}

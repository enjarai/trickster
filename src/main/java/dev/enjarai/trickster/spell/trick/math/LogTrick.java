package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class LogTrick extends DistortionTrick<LogTrick> {
    private static final NumberFragment E = new NumberFragment(Math.E);

    public LogTrick() {
        super(Pattern.of(4, 8, 7, 6, 3, 0, 1, 2), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER.optionalOfArg(), LogTrick::math, FragmentType.NUMBER));
    }

    public NumberFragment math(SpellContext ctx, NumberFragment number, Optional<NumberFragment> logBase) throws BlunderException {
        return new NumberFragment(Math.log(number.number()) / Math.log(logBase.orElse(E).number()));
    }
}

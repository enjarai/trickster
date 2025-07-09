package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.SubtractableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class SubtractTrick extends DistortionTrick<SubtractTrick> {
    public SubtractTrick() {
        super(Pattern.of(1, 4, 8, 7, 6, 4),
                Signature.of(ArgType.simple(SubtractableFragment.class).variadicOfArg().require().unpack(), SubtractTrick::run, RetType.simple(SubtractableFragment.class)));
    }

    public SubtractableFragment run(SpellContext ctx, List<SubtractableFragment> fragments) throws BlunderException {
        SubtractableFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.subtract(value);
            }
        }

        return result;
    }
}

package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.Tuple2;

import java.util.List;

public class IfElseTrick extends DistortionTrick<IfElseTrick> {
    public IfElseTrick() {
        super(Pattern.of(3, 4, 0, 2, 4, 5), Signature.of(FragmentType.BOOLEAN.pairedWith(ArgType.ANY).variadicOfArg(), ArgType.ANY, IfElseTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx, List<Tuple2<BooleanFragment, Fragment>> args, Fragment fallback) throws BlunderException {
        Fragment result = null;

        for (var pair : args) {
            if (pair._1().asBoolean()) {
                result = pair._2();
                break;
            }
        }

        return result == null ? fallback : result;
    }
}

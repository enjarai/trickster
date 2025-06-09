package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class IfElseTrick extends DistortionTrick<IfElseTrick> {
    public IfElseTrick() {
        super(Pattern.of(3, 4, 0, 2, 4, 5), Signature.of(variadic(Fragment.class, Fragment.class), ANY, IfElseTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx, List<Fragment> args, Fragment fallback) throws BlunderException {
        Fragment result = null;

        for (int i = 0; i < args.size(); i += 2) {
            if (args.get(i).asBoolean()) {
                result = args.get(i + 1);
                break;
            }
        }

        return result == null ? fallback : result;
    }
}

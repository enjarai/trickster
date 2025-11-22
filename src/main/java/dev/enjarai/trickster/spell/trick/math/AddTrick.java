package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AddableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class AddTrick extends DistortionTrick<AddTrick> {
    public AddTrick() {
        super(Pattern.of(7, 4, 0, 1, 2, 4), Signature.of(ArgType.simple(AddableFragment.class).variadicOfArg().require().unpack(), AddTrick::run, RetType.simple(AddableFragment.class)));
    }

    public AddableFragment run(SpellContext ctx, List<AddableFragment> fragments) {
        AddableFragment result = null;

        for (var value : fragments) {
            if (result == null) {
                result = value;
            } else {
                result = result.add(value);
            }
        }

        return result;
    }
}

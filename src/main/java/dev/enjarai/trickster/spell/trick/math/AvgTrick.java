package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AverageableFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class AvgTrick extends DistortionTrick<AvgTrick> {
    public AvgTrick() {
        super(
                Pattern.of(7, 4, 0, 1, 2, 4, 6, 7, 8),
                Signature.of(ArgType.simple(AverageableFragment.class).variadicOfArg().require().unpack(), AvgTrick::run, RetType.simple(AverageableFragment.class))
        );
    }

    public AverageableFragment run(SpellContext ctx, List<AverageableFragment> fragments) {
        return fragments.removeFirst().avg(fragments);
    }
}

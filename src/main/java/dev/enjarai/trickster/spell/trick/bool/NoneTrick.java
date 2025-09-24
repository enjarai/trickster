package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class NoneTrick extends DistortionTrick<NoneTrick> {
    public NoneTrick() {
        super(Pattern.of(7, 5, 2, 1, 3), Signature.of(FragmentType.BOOLEAN.variadicOfArg(), NoneTrick::run, FragmentType.BOOLEAN));
    }

    public BooleanFragment run(SpellContext ctx, List<BooleanFragment> fragments) throws BlunderException {
        return BooleanFragment.of(fragments.stream().noneMatch(Fragment::asBoolean));
    }
}

package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class AllTrick extends DistortionTrick<AllTrick> {
    public AllTrick() {
        super(Pattern.of(1, 4, 7, 5, 4, 3, 1), Signature.of(ANY_VARIADIC, AllTrick::run));
    }

    public Fragment run(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return BooleanFragment.of(fragments.stream().allMatch(Fragment::asBoolean));
    }
}

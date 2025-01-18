package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class MaxTrick extends DistortionTrick<MaxTrick> {
    public MaxTrick() {
        super(Pattern.of(3, 1, 5), Signature.of(variadic(FragmentType.NUMBER).require().unpack(), MaxTrick::run));
    }

    public Fragment run(SpellContext ctx, List<NumberFragment> numbers) throws BlunderException {
        return new NumberFragment(
                numbers.stream()
                        .mapToDouble(num -> num.number())
                        .max()
                        .orElseThrow(() -> new UnsupportedOperationException("MaxTrick somehow got an empty list of arguments"))
        );
    }
}

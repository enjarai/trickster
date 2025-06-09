package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class MinTrick extends DistortionTrick<MinTrick> {
    public MinTrick() {
        super(Pattern.of(3, 7, 5), Signature.of(variadic(FragmentType.NUMBER).require().unpack(), MinTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, List<NumberFragment> numbers) throws BlunderException {
        return new NumberFragment(
                numbers.stream()
                        .mapToDouble(num -> num.number())
                        .min()
                        .orElseThrow(() -> new UnsupportedOperationException("MinTrick somehow got an empty list of arguments"))
        );
    }
}

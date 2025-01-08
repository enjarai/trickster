package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.Optional;

public class DelayExecutionTrick extends Trick<DelayExecutionTrick> {
    public DelayExecutionTrick() {
        super(Pattern.of(0, 2, 4, 6, 8, 4, 0), Signature.of(FragmentType.NUMBER.optionalOf(), DelayExecutionTrick::run));
    }

    public Fragment run(SpellContext ctx, Optional<NumberFragment> number) throws BlunderException {
        var amount = number.map(n -> Math.round(n.asInt())).orElse(1);
        ctx.state().addDelay(amount);
        return new NumberFragment(amount);
    }
}

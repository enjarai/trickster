package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class DelayExecutionTrick extends Trick {
    public DelayExecutionTrick() {
        super(Pattern.of(0, 2, 4, 6, 8, 4, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var amount = supposeInput(fragments, FragmentType.NUMBER, 0).map(n -> Math.round(n.asInt())).orElse(1);
        ctx.state().addDelay(amount);
        return new NumberFragment(amount);
    }
}

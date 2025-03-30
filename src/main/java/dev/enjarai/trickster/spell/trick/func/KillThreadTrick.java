package dev.enjarai.trickster.spell.trick.func;

import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.OptionalUtils;

public class KillThreadTrick extends Trick<KillThreadTrick> {
    public KillThreadTrick() {
        super(Pattern.of(6, 3, 1, 4, 3, 7, 5, 4, 7, 6), Signature.of(FragmentType.NUMBER.optionalOf(), KillThreadTrick::run));
    }

    public Fragment run(SpellContext ctx, Optional<NumberFragment> index) throws BlunderException {
        return BooleanFragment.of(OptionalUtils.lift2(
                    (manager, i) -> {
                        if (i == ctx.data().getSlot())
                            ctx.data().kill();

                        return manager.kill(i);
                    },
                    ctx.source().getExecutionManager(),
                    index.map(NumberFragment::asInt).filter(i -> i >= 0).or(() -> OptionalUtils.conditional(i -> i >= 0, ctx.data().getSlot())))
                .orElse(false));
    }
}

package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class KillThreadTrick extends Trick<KillThreadTrick> {
    public KillThreadTrick() {
        super(Pattern.of(6, 3, 1, 4, 3, 7, 5, 4, 7, 6), Signature.of(FragmentType.NUMBER.optionalOfArg(), KillThreadTrick::run, FragmentType.BOOLEAN));
    }

    public BooleanFragment run(SpellContext ctx, Optional<NumberFragment> index) throws BlunderException {
        var manager = ctx.source().getExecutionManager().orElseThrow(() -> new IncompatibleSourceBlunder(this));

        if (index.isPresent()) {
            return BooleanFragment.of(manager.kill(index.get().asInt()));
        } else {
            ctx.data().kill();
            return BooleanFragment.TRUE;
        }
    }
}

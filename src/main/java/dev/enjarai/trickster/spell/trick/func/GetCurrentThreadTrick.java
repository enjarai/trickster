package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class GetCurrentThreadTrick extends Trick<GetCurrentThreadTrick> {
    public GetCurrentThreadTrick() {
        super(Pattern.of(1, 2, 7, 0, 1, 8, 7, 6, 1), Signature.of(GetCurrentThreadTrick::get, FragmentType.NUMBER.maybe()));
    }

    public Optional<NumberFragment> get(SpellContext ctx) throws BlunderException {
        return ctx.data().getSlot().map(NumberFragment::new);
    }
}

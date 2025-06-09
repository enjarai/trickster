package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.executor.AtomicSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class AtomicTrick extends Trick<AtomicTrick> {
    public AtomicTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 4, 2), Signature.of(FragmentType.SPELL_PART, ANY_VARIADIC, AtomicTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell, List<Fragment> args) throws BlunderException {
        return new AtomicSpellExecutor(this, ctx.data(), spell, ctx.state().recurseOrThrow(args));
    }
}

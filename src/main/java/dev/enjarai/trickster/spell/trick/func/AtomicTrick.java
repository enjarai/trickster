package dev.enjarai.trickster.spell.trick.func;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.executor.AtomicSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.ExecutionTrick;
import dev.enjarai.trickster.spell.type.Signature;

public class AtomicTrick extends ExecutionTrick<AtomicTrick> {
    public AtomicTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 4, 2), Signature.of(FragmentType.SPELL_PART, ANY_VARIADIC, AtomicTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell, List<Fragment> args) throws BlunderException {
        return new AtomicSpellExecutor(this, ctx.data(), spell, ctx.state().recurseOrThrow(args));
    }
}

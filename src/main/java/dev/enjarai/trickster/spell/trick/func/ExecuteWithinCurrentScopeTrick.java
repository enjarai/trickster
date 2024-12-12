package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.ExecutionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class ExecuteWithinCurrentScopeTrick extends ExecutionTrick<ExecuteWithinCurrentScopeTrick> {
    public ExecuteWithinCurrentScopeTrick() {
        super(Pattern.of(0, 1, 4, 5, 8, 7, 6, 3, 0), Signature.of(FragmentType.SPELL_PART, ExecuteWithinCurrentScopeTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell) throws BlunderException {
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(ctx.state().getArguments()));
    }
}

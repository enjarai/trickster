package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class ExecuteWithinCurrentScopeTrick extends Trick<ExecuteWithinCurrentScopeTrick> {
    public ExecuteWithinCurrentScopeTrick() {
        super(Pattern.of(0, 1, 4, 5, 8, 7, 6, 3, 0), Signature.of(FragmentType.SPELL_PART, ExecuteWithinCurrentScopeTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell) {
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(ctx.state().getArguments()));
    }
}

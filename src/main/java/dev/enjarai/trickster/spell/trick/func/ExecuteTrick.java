package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ExecuteTrick extends Trick<ExecuteTrick> {
    public ExecuteTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 4, 7), Signature.of(FragmentType.SPELL_PART, ANY_VARIADIC, ExecuteTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell, List<Fragment> args) throws BlunderException {
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(args));
    }
}

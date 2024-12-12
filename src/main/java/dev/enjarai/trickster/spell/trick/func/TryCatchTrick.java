package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.TryCatchSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.ExecutionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class TryCatchTrick extends ExecutionTrick<TryCatchTrick> {
    public TryCatchTrick() {
        super(Pattern.of(1, 6, 8, 1, 5, 2, 0, 3, 1, 4), Signature.of(FragmentType.SPELL_PART, FragmentType.SPELL_PART, ANY_VARIADIC, TryCatchTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart trySpell, SpellPart catchSpell, List<Fragment> args) throws BlunderException {
        return new TryCatchSpellExecutor(ctx, trySpell, catchSpell, args);
    }
}

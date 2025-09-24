package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.executor.TryCatchSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class TryCatchTrick extends Trick<TryCatchTrick> {
    public TryCatchTrick() {
        super(Pattern.of(1, 6, 8, 1, 5, 2, 0, 3, 1, 4), Signature.of(FragmentType.SPELL_PART, FragmentType.SPELL_PART, ArgType.ANY.variadicOfArg(), TryCatchTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart trySpell, SpellPart catchSpell, List<Fragment> args) throws BlunderException {
        return new TryCatchSpellExecutor(ctx, trySpell, catchSpell, args);
    }
}

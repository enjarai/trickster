package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FoldableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class FoldTrick extends Trick<FoldTrick> {
    public FoldTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 5, 8, 7, 4, 3), Signature.of(FragmentType.SPELL_PART, simple(FoldableFragment.class), ANY, FoldTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell, FoldableFragment collection, Fragment initial) throws BlunderException {
        return collection.fold(ctx, spell, initial);
    }
}

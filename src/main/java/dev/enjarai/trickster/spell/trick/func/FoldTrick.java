package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FoldableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.ExecutionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class FoldTrick extends ExecutionTrick<FoldTrick> {
    public FoldTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 5, 8, 7, 4, 3), Signature.of(FragmentType.SPELL_PART, simple(FoldableFragment.class), ANY, FoldTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, SpellPart spell, FoldableFragment collection, Fragment initial) throws BlunderException {
        return collection.fold(ctx, spell, initial);
    }
}

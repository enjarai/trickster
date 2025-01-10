package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class ReflectionTrick extends Trick<ReflectionTrick> {
    public ReflectionTrick() {
        super(Pattern.of(1, 5, 7, 3, 1), Signature.of(ReflectionTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        return new VectorFragment(ctx.source().getPos());
    }
}

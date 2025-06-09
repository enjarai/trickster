package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class FacingReflectionTrick extends Trick<FacingReflectionTrick> {
    public FacingReflectionTrick() {
        super(Pattern.of(3, 1, 5, 7, 3, 6, 4, 2, 5), Signature.of(FacingReflectionTrick::run, FragmentType.VECTOR));
    }

    public VectorFragment run(SpellContext ctx) throws BlunderException {
        return new VectorFragment(ctx.source().getFacing().orElseThrow(() -> new IncompatibleSourceBlunder(this)));
    }
}

package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class IfElseTrick extends DistortionTrick<IfElseTrick> {
    public IfElseTrick() {
        super(Pattern.of(3, 4, 0, 2, 4, 5), Signature.of(ANY, ANY, ANY, IfElseTrick::run));
    }

    public Fragment run(SpellContext ctx, Fragment condition, Fragment then, Fragment otherwise) throws BlunderException {
        if (condition.asBoolean()) {
            return then;
        } else {
            return otherwise;
        }
    }
}

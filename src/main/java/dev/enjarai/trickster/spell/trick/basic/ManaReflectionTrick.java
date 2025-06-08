package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class ManaReflectionTrick extends Trick<ManaReflectionTrick> {
    public ManaReflectionTrick() {
        super(Pattern.of(5, 7, 3, 1, 5, 2, 4, 0, 3, 4, 5), Signature.of(ManaReflectionTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx) throws BlunderException {
        return new NumberFragment(ctx.getManaPool().get(ctx.source().getWorld()));
    }
}

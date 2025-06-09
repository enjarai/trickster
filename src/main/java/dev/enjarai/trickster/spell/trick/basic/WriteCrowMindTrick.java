package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class WriteCrowMindTrick extends Trick<WriteCrowMindTrick> {
    public WriteCrowMindTrick() {
        super(Pattern.of(3, 6, 8, 5, 4, 0, 1, 2, 4), Signature.of(ANY, WriteCrowMindTrick::write, RetType.ANY));
    }

    public Fragment write(SpellContext ctx, Fragment fragment) throws BlunderException {
        ctx.source().setCrowMind(fragment);
        return fragment;
    }
}

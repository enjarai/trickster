package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

public class ReadCrowMindTrick extends Trick<ReadCrowMindTrick> {
    public ReadCrowMindTrick() {
        super(Pattern.of(5, 8, 6, 3, 4, 0, 1, 2, 4), Signature.of(ReadCrowMindTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx) {
        return ctx.source().getCrowMind();
    }
}

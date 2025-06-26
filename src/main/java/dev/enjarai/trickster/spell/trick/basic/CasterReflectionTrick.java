package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class CasterReflectionTrick extends Trick<CasterReflectionTrick> {
    public CasterReflectionTrick() {
        super(Pattern.of(4, 5, 7, 3, 4), Signature.of(CasterReflectionTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx) throws BlunderException {
        return ctx.source().getCaster().map(EntityFragment::from)
                .orElseThrow(() -> new NoPlayerBlunder(this));
    }
}

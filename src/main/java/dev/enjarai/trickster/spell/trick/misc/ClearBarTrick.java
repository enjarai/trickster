package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;

public class ClearBarTrick extends Trick<ClearBarTrick> {
    public ClearBarTrick() {
        super(Pattern.of(0, 6, 3, 0, 4, 8, 2, 5, 8), Signature.of(FragmentType.NUMBER, ClearBarTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id) throws BlunderException {
        ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this))
                .getComponent(ModEntityComponents.BARS).clearBar(id.asInt());

        return id;
    }
}

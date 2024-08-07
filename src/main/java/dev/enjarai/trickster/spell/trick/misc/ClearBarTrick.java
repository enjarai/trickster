package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NoPlayerBlunder;

import java.util.List;

public class ClearBarTrick extends Trick {
    public ClearBarTrick() {
        super(Pattern.of(0, 6, 3, 0, 4, 8, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var id = expectInput(fragments, FragmentType.NUMBER, 0);

        ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this))
                .getComponent(ModEntityCumponents.BARS).clearBar(id.asInt());

        return BooleanFragment.TRUE;
    }
}

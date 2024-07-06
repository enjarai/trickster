package dev.enjarai.trickster.spell.tricks.event;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;

import java.util.List;

public class WardTrick extends Trick {
    public WardTrick() {
        super(Pattern.of(1, 3, 0, 4, 3, 7, 6, 4, 7, 8, 4, 1, 5, 4, 2, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (ctx.getPlayer().isEmpty())
            throw new NoPlayerBlunder(this);

        var player = ctx.getPlayer().get();
        var wardHandler = expectInput(fragments, FragmentType.SPELL_PART, 0);

        ModEntityCumponents.WARD.get(player).register(wardHandler.deepClone());
        return VoidFragment.INSTANCE;
    }
}

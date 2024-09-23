package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class GetFleckTrick extends Trick {
    public GetFleckTrick() {
        super(Pattern.of());
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ListFragment(
            ctx.source().getPlayer().map(ModEntityCumponents.FLECKS::get).orElseThrow(() -> new NoPlayerBlunder(this))
            .getRenderFlecks().stream().<Fragment>map(n -> new NumberFragment(n.id())).toList()
        );
    }
}
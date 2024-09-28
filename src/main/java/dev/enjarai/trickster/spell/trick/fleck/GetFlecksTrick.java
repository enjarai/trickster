package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;

import java.util.List;

public class GetFlecksTrick extends Trick {
    public GetFlecksTrick() {
        super(Pattern.of(0, 4, 8, 7, 4, 1, 2, 4, 6)); // bluetooth symbol
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ListFragment(ModEntityComponents.FLECKS.get(supposeInput(fragments, FragmentType.ENTITY, 0)
                    .map(fragment -> fragment.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)))
                    .orElseGet(() -> ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this))))
                .getRenderFlecks()
                .stream()
                .map(fleck -> new NumberFragment(fleck.id()))
                .toList()
        );
    }
}

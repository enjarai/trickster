package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;

import java.util.Optional;

public class GetFlecksTrick extends Trick<GetFlecksTrick> {
    public GetFlecksTrick() {
        super(Pattern.of(0, 4, 8, 7, 4, 1, 2, 4, 6), Signature.of(FragmentType.ENTITY.optionalOf(), GetFlecksTrick::run)); // bluetooth symbol
    }

    public Fragment run(SpellContext ctx, Optional<EntityFragment> entity) throws BlunderException {
        return new ListFragment(
                ModEntityComponents.FLECKS.get(
                        entity
                                .map(fragment -> fragment.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)))
                                .orElseGet(() -> ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)))
                )
                        .getFlecks()
                        .keySet()
                        .intStream()
                        .<Fragment>mapToObj(NumberFragment::new)
                        .toList()
        );
    }
}

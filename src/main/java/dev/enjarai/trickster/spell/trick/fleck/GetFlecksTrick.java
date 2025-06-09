package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class GetFlecksTrick extends Trick<GetFlecksTrick> {
    public GetFlecksTrick() {
        super(Pattern.of(0, 4, 8, 7, 4, 1, 2, 4, 6), Signature.of(FragmentType.ENTITY.optionalOf(), GetFlecksTrick::run, FragmentType.NUMBER.listOf())); // bluetooth symbol
    }

    public List<NumberFragment> run(SpellContext ctx, Optional<EntityFragment> entity) throws BlunderException {
        return ModEntityComponents.FLECKS.get(
                entity
                        .map(fragment -> fragment.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)))
                        .orElseGet(() -> ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)))
        )
                .getFlecks()
                .keySet()
                .intStream()
                .mapToObj(NumberFragment::new)
                .toList();
    }
}

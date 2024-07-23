package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;

import java.util.List;

public class GetVelocityTrick extends Trick {
    public GetVelocityTrick() {
        super(Pattern.of(1, 8, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new VectorFragment(expectInput(fragments, FragmentType.ENTITY, 0)
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this))
                .getVelocity()
                .toVector3d());
    }
}

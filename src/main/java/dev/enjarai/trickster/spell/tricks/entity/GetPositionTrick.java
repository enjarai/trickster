package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;

import java.util.List;

public class GetPositionTrick extends Trick {
    public GetPositionTrick() {
        super(Pattern.of(1, 4, 7, 3, 1, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0);

        var facing = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getPos();

        return new VectorFragment(facing.toVector3d());
    }
}

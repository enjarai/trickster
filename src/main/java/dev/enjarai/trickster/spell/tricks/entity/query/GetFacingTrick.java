package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.joml.Vector3d;

import java.util.List;

public class GetFacingTrick extends AbstractLivingEntityQueryTrick {
    public GetFacingTrick() {
        super(Pattern.of(3, 2, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var facing = getLivingEntity(ctx, fragments, 0).getRotationVector();

        return new VectorFragment(new Vector3d(facing.x, facing.y, facing.z));
    }
}

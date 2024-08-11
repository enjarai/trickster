package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import org.joml.Vector3d;

import java.util.List;

public class GetEyePositionTrick extends Trick {
    public GetEyePositionTrick() {
        super(Pattern.of(3, 4, 5, 7, 3, 1, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0);

        var pos = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getEyePos();

        return new VectorFragment(new Vector3d(pos.x, pos.y, pos.z));
    }
}

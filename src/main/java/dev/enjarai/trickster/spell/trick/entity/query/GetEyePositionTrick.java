package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import org.joml.Vector3d;

public class GetEyePositionTrick extends Trick<GetEyePositionTrick> {
    public GetEyePositionTrick() {
        super(Pattern.of(3, 4, 5, 7, 3, 1, 5), Signature.of(FragmentType.ENTITY, GetEyePositionTrick::run));
    }

    public Fragment run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        var pos = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getEyePos();
        return new VectorFragment(new Vector3d(pos.x, pos.y, pos.z));
    }
}

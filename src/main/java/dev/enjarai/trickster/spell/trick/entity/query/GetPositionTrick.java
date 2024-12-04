package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.type.TrickSignature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import org.joml.Vector3d;

public class GetPositionTrick extends Trick<GetPositionTrick> {
    public GetPositionTrick() {
        super(Pattern.of(1, 4, 7, 3, 1, 5, 7), TrickSignature.of(FragmentType.ENTITY, GetPositionTrick::run));
    }

    public Fragment run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        var pos = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getPos();
        return new VectorFragment(new Vector3d(pos.x, pos.y, pos.z));
    }
}

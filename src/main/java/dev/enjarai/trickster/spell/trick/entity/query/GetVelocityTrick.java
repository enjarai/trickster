package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetVelocityTrick extends Trick<GetVelocityTrick> {
    public GetVelocityTrick() {
        super(Pattern.of(1, 8, 3), Signature.of(FragmentType.ENTITY, GetVelocityTrick::run, FragmentType.VECTOR));
    }

    public VectorFragment run(SpellContext ctx, EntityFragment entity) {
        return new VectorFragment(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getVelocity().toVector3d());
    }
}

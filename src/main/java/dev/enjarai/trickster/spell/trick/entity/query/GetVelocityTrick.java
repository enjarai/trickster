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

public class GetVelocityTrick extends Trick<GetVelocityTrick> {
    public GetVelocityTrick() {
        super(Pattern.of(1, 8, 3), Signature.of(FragmentType.ENTITY, GetVelocityTrick::run));
    }

    public Fragment run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        return new VectorFragment(entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getVelocity().toVector3d());
    }
}

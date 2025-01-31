package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.EntityTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;

public class GetEntityTypeTrick extends Trick<GetEntityTypeTrick> {
    public GetEntityTypeTrick() {
        super(Pattern.of(2, 4, 1, 0, 3, 4, 6), Signature.of(FragmentType.ENTITY, GetEntityTypeTrick::run));
    }

    public Fragment run(SpellContext ctx, EntityFragment target) throws BlunderException {
        return new EntityTypeFragment(target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getType());
    }
}

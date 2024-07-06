package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;

import java.util.List;

public class getEntityTypeTrick extends Trick {
    public getEntityTypeTrick() {
        super(pattern);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var entity = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        return new EntityTypeFragment(entity.getType());
    }
}

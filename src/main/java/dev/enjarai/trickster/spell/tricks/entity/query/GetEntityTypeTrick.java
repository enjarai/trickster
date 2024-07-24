package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.EntityTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;

import java.util.List;

public class GetEntityTypeTrick extends Trick {
    public GetEntityTypeTrick() {
        super(Pattern.of(2, 4, 1, 0, 3, 4, 6));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var entity = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        return new EntityTypeFragment(entity.getType());
    }
}

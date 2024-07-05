package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;

import java.util.List;

public class AddVelocityTrick extends Trick {
    public AddVelocityTrick() {
        super(Pattern.of(4, 6, 0, 1, 2, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0);
        var velocity = expectInput(fragments, FragmentType.VECTOR, 1);

        var player = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        ctx.useMana(this, (float)velocity.vector().length() * 4);

        player.addVelocity(velocity.vector().x(), velocity.vector().y(), velocity.vector().z());
        player.velocityModified = true;

        return VoidFragment.INSTANCE;
    }
}

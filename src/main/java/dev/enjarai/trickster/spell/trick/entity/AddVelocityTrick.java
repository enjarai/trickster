package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;

import java.util.List;

public class AddVelocityTrick extends Trick {
    public AddVelocityTrick() {
        super(Pattern.of(4, 6, 0, 1, 2, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0)
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));

        fragments = tryWard(ctx, target, fragments);

        var velocity = expectInput(fragments, FragmentType.VECTOR, 1);
        var lengthSquared = velocity.vector().lengthSquared();
        ctx.useMana(this, 3f + (float) lengthSquared * 2f);
        target.addVelocity(velocity.vector().x(), velocity.vector().y(), velocity.vector().z());
        target.limitFallDistance();
        target.velocityModified = true;

        return VoidFragment.INSTANCE;
    }
}

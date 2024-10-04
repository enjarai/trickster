package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3d;

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
        var velocity = expectInput(fragments, FragmentType.VECTOR, 1);
        tryWard(ctx, target, fragments);

        var lengthSquared = velocity.vector().lengthSquared();
        ctx.useMana(this, 3f + (float) lengthSquared * 2f);

        var vector = velocity.vector();
        if (target instanceof PlayerEntity && ModEntityComponents.GRACE.get(target).isInGrace("gravity")) {
            vector = vector.add(0, -target.getFinalGravity(), 0, new Vector3d());
        }

        target.addVelocity(vector.x(), vector.y(), vector.z());
        target.limitFallDistance();
        target.velocityModified = true;

        if (target instanceof PlayerEntity && vector.x() >= 0) {
            ModEntityComponents.GRACE.get(target).triggerGrace("gravity", 2);
        }

        return EntityFragment.from(target);
    }
}

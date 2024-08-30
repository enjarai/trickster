package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

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
        target.addVelocity(velocity.vector().x(), velocity.vector().y(), velocity.vector().z());
        target.limitFallDistance();
        target.velocityModified = true;

        if (target instanceof PlayerEntity) {
            ModEntityCumponents.GRACE.get(target).triggerGrace("gravity", 2);
        }

        return VoidFragment.INSTANCE;
    }
}

package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Box;

import java.util.List;

public class RaycastEntityTrick extends AbstractLivingEntityQueryTrick {
    public RaycastEntityTrick() {
        super(Pattern.of(3, 4, 5, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = getLivingEntity(ctx, fragments, 0);
        var pos1 = entity.getEyePos();
        var pos2 = entity.getEyePos().add(entity.getRotationVector().multiply(128d));
        var hit = ProjectileUtil.raycast(
                entity, pos1, pos2,
                new Box(pos1, pos2), e -> true, 128
        );

        return hit == null ? VoidFragment.INSTANCE : EntityFragment.from(hit.getEntity());
    }
}

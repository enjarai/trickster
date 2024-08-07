package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
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
        var pos2 = entity.getEyePos().add(entity.getRotationVector().multiply(64d));
        var hit = ProjectileUtil.raycast(
                entity, pos1, pos2,
                new Box(pos1, pos2), e -> true, 64 * 64
        );

        return hit == null ? VoidFragment.INSTANCE : EntityFragment.from(hit.getEntity());
    }
}

package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RaycastEntityTrick extends AbstractLivingEntityQueryTrick {
    public RaycastEntityTrick() {
        super(Pattern.of(3, 4, 5, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Optional<Entity> entity = Optional.empty();
        Vec3d vec1;
        Vec3d vec2;

        try {
            var entity1 = getLivingEntity(ctx, fragments, 0);
            vec1 = entity1.getEyePos();
            vec2 = entity1.getRotationVector();
            entity = Optional.of(entity1);
        } catch (IncorrectFragmentBlunder blunder) {
            var v1 = expectInput(fragments, FragmentType.VECTOR, 0).vector();
            var v2 = expectInput(fragments, FragmentType.VECTOR, 1).vector();
            vec1 = new Vec3d(v1.x(), v1.y(), v1.z());
            vec2 = new Vec3d(v2.x(), v2.y(), v2.z());
        }

        var pos2 = vec1.add(vec2.multiply(64d));
        var hit = raycast(ctx.source().getWorld(), entity, vec1, pos2, new Box(vec1, pos2), 64 * 64);

        return hit == null ? VoidFragment.INSTANCE : EntityFragment.from(hit.getEntity());
    }

    // I needed some changes from ProjectileUtil's impl -- Aurora
    @Nullable
    private static EntityHitResult raycast(World world, Optional<Entity> entity, Vec3d min, Vec3d max, Box box, double maxDistance) {
        double d = maxDistance;
        Entity entity2 = null;
        Vec3d vec3d = null;
        Iterator var12 = world.getOtherEntities(entity.orElse(null), box, e -> true).iterator();

        while(true) {
            while(var12.hasNext()) {
                Entity entity3 = (Entity)var12.next();
                Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin());
                Optional<Vec3d> optional = box2.raycast(min, max);
                if (box2.contains(min)) {
                    if (d >= 0.0) {
                        entity2 = entity3;
                        vec3d = optional.orElse(min);
                        d = 0.0;
                    }
                } else if (optional.isPresent()) {
                    Vec3d vec3d2 = optional.get();
                    double e = min.squaredDistanceTo(vec3d2);
                    if (e < d || d == 0.0) {
                        if (entity.isPresent() && entity3.getRootVehicle() == entity.get().getRootVehicle()) {
                            if (d == 0.0) {
                                entity2 = entity3;
                                vec3d = vec3d2;
                            }
                        } else {
                            entity2 = entity3;
                            vec3d = vec3d2;
                            d = e;
                        }
                    }
                }
            }

            if (entity2 == null) {
                return null;
            }

            return new EntityHitResult(entity2, vec3d);
        }
    }
}

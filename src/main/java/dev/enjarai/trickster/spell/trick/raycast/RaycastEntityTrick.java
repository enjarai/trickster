package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;

public class RaycastEntityTrick extends AbstractRaycastTrick {
    public RaycastEntityTrick() {
        super(Pattern.of(3, 4, 5, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction) throws BlunderException {
        var multipliedDirection = position.add(direction.multiply(64d));
        var hit = raycast(ctx.source().getWorld(), entity, position, multipliedDirection, new Box(position, multipliedDirection), 64 * 64);
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

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

import java.util.Optional;

public class RaycastEntityTrick extends AbstractRaycastTrick {
    public RaycastEntityTrick() {
        super(Pattern.of(3, 4, 5, 8, 4));
    }

    @Override
    public Fragment run(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, Optional<Fragment> bool) throws BlunderException {
        var multipliedDirection = position.add(direction.multiply(64d));
        var hit = raycast(ctx.source().getWorld(), entity, position, multipliedDirection, new Box(position, multipliedDirection), 64 * 64);
        return hit == null ? VoidFragment.INSTANCE : EntityFragment.from(hit.getEntity());
    }

    // I needed some changes from ProjectileUtil's impl -- Aurora
    @Nullable
    private static EntityHitResult raycast(World world, Optional<Entity> entity, Vec3d min, Vec3d max, Box box, double maxDistance) {
        double distance = maxDistance;
        Entity foundEntity = null;
        Vec3d pos = null;

        for (Entity entityToBeMaybeFound : world.getOtherEntities(entity.orElse(null), box, e -> true)) {
            Box box2 = entityToBeMaybeFound.getBoundingBox().expand(entityToBeMaybeFound.getTargetingMargin());
            Optional<Vec3d> perhapsRaycastEntityPos = box2.raycast(min, max);
            if (box2.contains(min)) {
                if (distance >= 0.0) {
                    foundEntity = entityToBeMaybeFound;
                    pos = perhapsRaycastEntityPos.orElse(min);
                    distance = 0.0;
                }
            } else if (perhapsRaycastEntityPos.isPresent()) {
                Vec3d raycastEntityPos = perhapsRaycastEntityPos.get();
                double squareDistance = min.squaredDistanceTo(raycastEntityPos);
                if (squareDistance < distance || distance == 0.0) {
                    if (entity.isPresent() && entityToBeMaybeFound.getRootVehicle() == entity.get().getRootVehicle()) {
                        if (distance == 0.0) {
                            foundEntity = entityToBeMaybeFound;
                            pos = raycastEntityPos;
                        }
                    } else {
                        foundEntity = entityToBeMaybeFound;
                        pos = raycastEntityPos;
                        distance = squareDistance;
                    }
                }
            }
        }

        if (foundEntity == null) {
            return null;
        }

        return new EntityHitResult(foundEntity, pos);
    }
}

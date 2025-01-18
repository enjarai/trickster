package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;

public abstract class AbstractRaycastBlockTrick extends AbstractRaycastTrick {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    public Fragment run(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, Optional<Fragment> bool) throws BlunderException {
        boolean includeFluids = bool.orElse(BooleanFragment.FALSE).asBoolean();

        return activate(
                ctx.source().getWorld().raycast(
                        new RaycastContext(
                                position,
                                position.add(direction.multiply(64d)),
                                RaycastContext.ShapeType.OUTLINE,
                                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                                entity.map(ShapeContext::of).orElseGet(ShapeContext::absent)
                        )
                )
        );
    }

    public abstract Fragment activate(BlockHitResult hit) throws BlunderException;
}

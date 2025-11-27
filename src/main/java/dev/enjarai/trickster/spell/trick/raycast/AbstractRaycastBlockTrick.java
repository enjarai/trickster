package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;

public abstract class AbstractRaycastBlockTrick extends AbstractRaycastTrick<VectorFragment> {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern, FragmentType.VECTOR);
    }

    public Optional<VectorFragment> run(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, Optional<BooleanFragment> bool) {
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

    public abstract Optional<VectorFragment> activate(BlockHitResult hit) throws BlunderException;
}

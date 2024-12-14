package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRaycastBlockTrick extends AbstractRaycastTrick {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Fragment activate(List<Fragment> fragments, SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction) throws BlunderException {
        boolean includeFluids = supposeInput(fragments, entity.isPresent() ? 1 : 2).orElse(BooleanFragment.FALSE).asBoolean();

        return activate(ctx.source().getWorld().raycast(new RaycastContext(position,
                position.add(direction.multiply(64d)),
                RaycastContext.ShapeType.OUTLINE,
                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                entity.map(ShapeContext::of).orElseGet(ShapeContext::absent)
        )));
    }

    public abstract Fragment activate(BlockHitResult hit) throws BlunderException;
}

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

public abstract class AbstractRaycastBlockTrick extends AbstractRaycastTrick<Optional<BooleanFragment>> {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    @Override
    protected Fragment extraContext(List<Fragment> fragments, SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction) {
        var fluids = supposeInput(fragments, FragmentType.BOOLEAN, 1);
        return activate(ctx, entity, position, direction, fluids);
    }

    @Override
    public Fragment activate(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, Optional<BooleanFragment> extraContext) throws BlunderException {
        boolean includeFluids = extraContext.orElse(BooleanFragment.FALSE).asBoolean();
        return activate(ctx.source().getWorld().raycast(new RaycastContext(position,
                position.add(direction.multiply(64d)),
                RaycastContext.ShapeType.OUTLINE,
                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                entity.map(ShapeContext::of).orElseGet(ShapeContext::absent)
        )));
    }

    public abstract Fragment activate(BlockHitResult hit) throws BlunderException;
}

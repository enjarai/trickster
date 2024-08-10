package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public abstract class AbstractRaycastBlockTrick extends AbstractLivingEntityQueryTrick {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    public BlockHitResult getHit(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        ShapeContext shapeCtx = ShapeContext.absent();
        Vec3d vec1;
        Vec3d vec2;

        try {
            var entity = getLivingEntity(ctx, fragments, 0);
            shapeCtx = ShapeContext.of(entity);
            vec1 = entity.getEyePos();
            vec2 = entity.getRotationVector();
        } catch (IncorrectFragmentBlunder blunder) {
            var v1 = expectInput(fragments, FragmentType.VECTOR, 0).vector();
            var v2 = expectInput(fragments, FragmentType.VECTOR, 1).vector();
            vec1 = new Vec3d(v1.x(), v1.y(), v1.z());
            vec2 = new Vec3d(v2.x(), v2.y(), v2.z());
        }

        return ctx.source().getWorld().raycast(new RaycastContext(
                vec1, vec1.add(vec2.multiply(64d)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, shapeCtx
        ));
    }

    @Override
    public abstract Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException;
}
package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;

import java.util.List;

public abstract class AbstractRaycastBlockTrick extends AbstractLivingEntityQueryTrick {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    public BlockHitResult getHit(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = getLivingEntity(ctx, fragments, 0);

        return entity.getWorld().raycast(new RaycastContext(
                entity.getEyePos(), entity.getEyePos().add(entity.getRotationVector().multiply(128d)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity
        ));
    }

    @Override
    public abstract Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException;
}
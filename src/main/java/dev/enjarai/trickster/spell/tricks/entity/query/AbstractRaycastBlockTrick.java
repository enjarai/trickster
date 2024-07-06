package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;

import java.util.List;

public abstract class AbstractRaycastBlockTrick extends Trick {
    public AbstractRaycastBlockTrick(Pattern pattern) {
        super(pattern);
    }

    public BlockHitResult getHit(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entityArg = expectInput(fragments, FragmentType.ENTITY, 0);

        var entity = entityArg.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        return entity.getWorld().raycast(new RaycastContext(
                entity.getEyePos(), entity.getEyePos().add(entity.getRotationVector().multiply(128d)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity
        ));
    }

    @Override
    public abstract Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException;
}
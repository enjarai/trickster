package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class RaycastTrick extends Trick {
    public RaycastTrick() {
        super(Pattern.of(3, 4, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entityArg = expectInput(fragments, FragmentType.ENTITY, 0);

        var entity = entityArg.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        var hit = entity.getWorld().raycast(new RaycastContext(
                entity.getEyePos(), entity.getEyePos().add(entity.getRotationVector().multiply(128d)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity
        ));
        return hit.getType() == HitResult.Type.MISS ? VoidFragment.INSTANCE : VectorFragment.of(hit.getBlockPos());
    }
}

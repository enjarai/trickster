package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.util.hit.HitResult;

import java.util.List;

public class RaycastBlockPosTrick extends AbstractRaycastBlockTrick {
    public RaycastBlockPosTrick() {
        super(Pattern.of(3, 4, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var hit = getHit(ctx, fragments);
        return hit.getType() == HitResult.Type.MISS ? VoidFragment.INSTANCE : VectorFragment.of(hit.getBlockPos());
    }
}

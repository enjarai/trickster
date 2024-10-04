package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class RaycastBlockPosTrick extends AbstractRaycastBlockTrick {
    public RaycastBlockPosTrick() {
        super(Pattern.of(3, 4, 5, 2, 4));
    }

    @Override
    public Fragment activate(BlockHitResult hit) throws BlunderException {
        return hit.getType() == HitResult.Type.MISS ? VoidFragment.INSTANCE : VectorFragment.of(hit.getBlockPos());
    }
}

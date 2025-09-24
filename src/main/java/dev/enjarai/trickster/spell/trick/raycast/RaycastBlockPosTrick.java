package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;

public class RaycastBlockPosTrick extends AbstractRaycastBlockTrick {
    public RaycastBlockPosTrick() {
        super(Pattern.of(3, 4, 5, 2, 4));
    }

    @Override
    public Optional<VectorFragment> activate(BlockHitResult hit) throws BlunderException {
        return hit.getType() == HitResult.Type.MISS ? Optional.empty() : Optional.of(VectorFragment.of(hit.getBlockPos()));
    }
}

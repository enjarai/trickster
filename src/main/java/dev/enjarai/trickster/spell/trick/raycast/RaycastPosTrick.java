package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;

public class RaycastPosTrick extends AbstractRaycastBlockTrick {
    public RaycastPosTrick() {
        super(Pattern.of(3, 4, 5, 2, 8, 5));
    }

    @Override
    public Optional<VectorFragment> activate(BlockHitResult hit) throws BlunderException {
        return hit.getType() == HitResult.Type.MISS ? Optional.empty() : Optional.of(VectorFragment.of(hit.getPos()));
    }
}

package dev.enjarai.trickster.spell.trick.particle;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class HighlightTrick extends Trick<HighlightTrick> {
    public HighlightTrick() {
        super(Pattern.of(2, 0, 1, 2, 3, 4, 5, 8, 7, 6, 3), Signature.of(variadic(FragmentType.VECTOR).required().unpack(), HighlightTrick::run));
    }

    public Fragment run(SpellContext ctx, List<VectorFragment> positions) throws BlunderException {
        for (var pos : positions) {
            var block = pos.toBlockPos().toCenterPos();
            ctx.source().getWorld().spawnParticles(
                    ModParticles.PROTECTED_BLOCK, block.x, block.y, block.z,
                    1, 0, 0, 0, 0
            );
        }

        return positions.get(0);
    }
}

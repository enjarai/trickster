package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.block.SpellCircleBlock;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.SpellCircleSpellSource;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import org.joml.Vector3d;

import java.util.List;

public class FacingReflectionTrick extends Trick {
    public FacingReflectionTrick() {
        super(Pattern.of(3, 1, 5, 7, 3, 6, 4, 2, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (ctx.source() instanceof SpellCircleSpellSource blockSource) {
            return new VectorFragment(blockSource.blockEntity.getCachedState()
                    .get(SpellCircleBlock.FACING).getUnitVector().get(new Vector3d()));
        }

        return new VectorFragment(ctx.source().getCaster()
                .orElseThrow(() -> new IncompatibleSourceBlunder(this))
                .getRotationVector().toVector3d());
    }
}

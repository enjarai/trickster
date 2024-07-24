package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.block.SpellCircleBlock;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.BlockSpellSource;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncompatibleSourceBlunder;
import org.joml.Vector3d;

import java.util.List;

public class FacingReflectionTrick extends Trick {
    public FacingReflectionTrick() {
        super(Pattern.of(3, 1, 5, 7, 3, 6, 4, 2, 5));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        if (ctx instanceof BlockSpellSource blockCtx) {
            return new VectorFragment(blockCtx.blockEntity.getCachedState()
                    .get(SpellCircleBlock.FACING).getUnitVector().get(new Vector3d()));
        }

        return new VectorFragment(ctx.getCaster()
                .orElseThrow(() -> new IncompatibleSourceBlunder(this))
                .getRotationVector().toVector3d());
    }
}

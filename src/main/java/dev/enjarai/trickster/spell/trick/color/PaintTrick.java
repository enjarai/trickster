package dev.enjarai.trickster.spell.trick.color;

import dev.enjarai.trickster.block.SpellColoredBlockEntity;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class PaintTrick extends Trick<PaintTrick> {
    public PaintTrick() {
        super(Pattern.of(4, 6, 7, 4, 3, 0, 1, 2, 5, 8, 7), Signature.of(FragmentType.VECTOR, FragmentType.COLOR.variadicOfArg().require(), PaintTrick::run, FragmentType.VECTOR));
    }

    public VectorFragment run(SpellContext ctx, VectorFragment pos, List<ColorFragment> colors) {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();

        expectCanBuild(ctx, blockPos);

        if (world.getBlockEntity(blockPos) instanceof SpellColoredBlockEntity ent) {
            var cs = new int[colors.size()];
            for (int i = 0; i < cs.length; i++) {
                cs[i] = colors.get(i).color();
            }
            ent.setColors(cs);
            return pos;
        }
        throw new BlockInvalidBlunder(this);
    }
}

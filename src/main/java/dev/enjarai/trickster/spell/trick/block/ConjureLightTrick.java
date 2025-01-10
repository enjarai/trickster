package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.LightBlock;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.registry.tag.FluidTags;

public class ConjureLightTrick extends Trick<ConjureLightTrick> {
    public ConjureLightTrick() {
        super(Pattern.of(8, 4, 0, 1, 2, 0), Signature.of(FragmentType.VECTOR, ConjureLightTrick::conjure));
    }

    public Fragment conjure(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        var waterlogged = false;

        if (!world.getBlockState(blockPos).isAir()) {
            if (!world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                throw new BlockOccupiedBlunder(this, pos);
            }
            waterlogged = true;
        }

        ctx.useMana(this, 20);
        world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LightBlock.WATERLOGGED, waterlogged));

        return pos;
    }
}

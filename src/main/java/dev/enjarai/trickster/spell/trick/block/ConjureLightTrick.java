package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.LightBlock;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooLargeBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;

import java.util.Optional;

public class ConjureLightTrick extends Trick<ConjureLightTrick> {
    public ConjureLightTrick() {
        super(Pattern.of(8, 4, 0, 1, 2, 0), Signature.of(FragmentType.VECTOR, FragmentType.NUMBER.optionalOfArg(), ConjureLightTrick::conjure, FragmentType.VECTOR));
    }

    public VectorFragment conjure(SpellContext ctx, VectorFragment pos, Optional<NumberFragment> levelOptional) {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        BlockState state = world.getBlockState(blockPos);
        var waterlogged = state.getFluidState().getFluid() == Fluids.WATER;
        var isWater = state.getFluidState().isIn(FluidTags.WATER);
        var dry = state.getFluidState().getFluid() == Fluids.EMPTY;

        // Blunder if block can't be replaced or if it has a fluid that isn't water
        if (!state.isReplaceable() || (!state.isAir() && !isWater && !dry)) {
            throw new BlockOccupiedBlunder(this, pos);
        }

        int level = levelOptional.map(NumberFragment::asInt).orElse(15);
        if (level < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }
        if (level > 15) {
            throw new NumberTooLargeBlunder(this, 15);
        }

        ctx.useMana(this, 20);
        world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LightBlock.LIGHT_LEVEL, level).with(LightBlock.WATERLOGGED, waterlogged));

        return pos;
    }
}

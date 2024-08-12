package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.LightBlock;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.trick.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ConjureLightTrick extends Trick {
    public ConjureLightTrick() {
        super(Pattern.of(8, 4, 0, 1, 2, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        var waterlogged = false;

        if (!world.getBlockState(blockPos).isAir()) {
            if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                throw new BlockOccupiedBlunder(this, pos);
            }
            waterlogged = true;
        }

        ctx.useMana(this, 20);
        world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LightBlock.WATERLOGGED, waterlogged));

        return VoidFragment.INSTANCE;
    }
}

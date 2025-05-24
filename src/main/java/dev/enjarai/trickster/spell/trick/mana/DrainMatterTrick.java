package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;

public class DrainMatterTrick extends Trick<DrainMatterTrick> {
    public DrainMatterTrick() {
        super(Pattern.of(0, 2, 8, 6, 0, 1, 2, 5, 4, 3, 0), Signature.of(FragmentType.VECTOR, DrainMatterTrick::run));
    }

    public Fragment run(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var world = ctx.source().getWorld();
        var blockPos = pos.toBlockPos();
        var state = world.getBlockState(blockPos);
        float amount = DataLoader
                .getStateToManaConversionLoader()
                .convert(state)
                .orElseThrow(() -> new BlockInvalidBlunder(this, state.getBlock()));

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0);

        if (world.getFluidState(blockPos).isOf(Fluids.WATER)) {
            world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
        } else {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        }

        return new NumberFragment(ctx.source().getManaPool().refill(amount, world));
    }
}

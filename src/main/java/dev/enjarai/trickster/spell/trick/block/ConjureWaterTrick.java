package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class ConjureWaterTrick extends Trick<ConjureWaterTrick> {
    public ConjureWaterTrick() {
        super(Pattern.of(3, 0, 4, 2, 5), Signature.of(FragmentType.VECTOR, ConjureWaterTrick::conjure));
    }

    public Fragment conjure(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var bucket = Items.WATER_BUCKET;
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        if (!(world.getBlockState(blockPos).isAir()
                || world.getBlockState(blockPos).getBlock() instanceof Waterloggable
                || world.getBlockState(blockPos).isOf(Blocks.CAULDRON)
                || world.getFluidState(blockPos).isIn(FluidTags.WATER))
        ) {
            throw new BlockOccupiedBlunder(this, pos);
        }

        var state = world.getBlockState(blockPos);
        ctx.useMana(this, 15);

        if (state.getBlock() == Blocks.CAULDRON) {
            world.setBlockState(blockPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, LeveledCauldronBlock.MAX_LEVEL), 3);
        } else if (!tryPlaceWater(
                world,
                blockPos
        ) && bucket instanceof BucketItem) {
            ((BucketItem) bucket).placeFluid(null, world, blockPos, null);
        }

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return pos;
    }

    private boolean tryPlaceWater(World world, BlockPos pos) {
        Storage<FluidVariant> target = FluidStorage.SIDED.find(world, pos, Direction.UP);
        if (target == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            long insertedAmount = target.insert(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction);
            if (insertedAmount > 0) {
                transaction.commit();
                return true;
            }
        }
        return false;
    }
}

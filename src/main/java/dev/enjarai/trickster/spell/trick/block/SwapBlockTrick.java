package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.OverlapBlunder;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class SwapBlockTrick extends Trick {
    public SwapBlockTrick() {
        super(Pattern.of(3, 4, 5, 8, 4, 0, 3, 6, 4, 2, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos1 = expectInput(fragments, FragmentType.VECTOR, 0);
        var pos2 = expectInput(fragments, FragmentType.VECTOR, 1);
        var blockPos1 = pos1.toBlockPos();
        var blockPos2 = pos2.toBlockPos();
        var world = ctx.source().getWorld();

        if (blockPos1.equals(blockPos2)) {
            throw new OverlapBlunder(this, pos1, pos2);
        }

        expectCanBuild(ctx, blockPos1, blockPos2);

        var state1 = world.getBlockState(blockPos1);
        var state2 = world.getBlockState(blockPos2);

        if (state1.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos1);
        }
        if (state2.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos2);
        }

        if (!Trickster.CONFIG.allowSwapBedrock()) {
            if (state1.getHardness(world, blockPos1) < 0) {
                throw new BlockInvalidBlunder(this);
            }
            if (state2.getHardness(world, blockPos2) < 0) {
                throw new BlockInvalidBlunder(this);
            }
        }

        ctx.useMana(this, (float) (60 + (pos1.vector().distance(pos2.vector()))));

        NbtCompound blockEntity1Nbt = null;
        NbtCompound blockEntity2Nbt = null;
        var blockEntity1 = world.getBlockEntity(blockPos1);
        var blockEntity2 = world.getBlockEntity(blockPos2);
        if (blockEntity1 != null) {
            blockEntity1Nbt = blockEntity1.createNbt(world.getRegistryManager());
            blockEntity1.read(new NbtCompound(), world.getRegistryManager());
        }
        if (blockEntity2 != null) {
            blockEntity2Nbt = blockEntity2.createNbt(world.getRegistryManager());
            blockEntity2.read(new NbtCompound(), world.getRegistryManager());
        }

        world.setBlockState(blockPos1, state2);
        world.setBlockState(blockPos2, state1);

        if (blockEntity1Nbt != null) {
            world.getBlockEntity(blockPos2).read(blockEntity1Nbt, world.getRegistryManager());
        }
        if (blockEntity2Nbt != null) {
            world.getBlockEntity(blockPos1).read(blockEntity2Nbt, world.getRegistryManager());
        }

        var particlePos1 = blockPos1.toCenterPos();
        var particlePos2 = blockPos2.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos1.x, particlePos1.y, particlePos1.z,
                1, 0, 0, 0, 0
        );
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos2.x, particlePos2.y, particlePos2.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}

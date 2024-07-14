package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.OverlapBlunder;
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

        if (blockPos1.equals(blockPos2)) {
            throw new OverlapBlunder(this, pos1, pos2);
        }

        expectCanBuild(ctx, blockPos1, blockPos2);

        var state1 = ctx.getWorld().getBlockState(blockPos1);
        var state2 = ctx.getWorld().getBlockState(blockPos2);

        if (state1.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos1);
        }
        if (state2.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos2);
        }

        ctx.useMana(this, (float)(60 + (pos1.vector().distance(pos2.vector()))));

        NbtCompound blockEntity1Nbt = null;
        NbtCompound blockEntity2Nbt = null;
        var blockEntity1 = ctx.getWorld().getBlockEntity(blockPos1);
        var blockEntity2 = ctx.getWorld().getBlockEntity(blockPos2);
        if (blockEntity1 != null) {
            blockEntity1Nbt = blockEntity1.createNbt(ctx.getWorld().getRegistryManager());
            blockEntity1.read(new NbtCompound(), ctx.getWorld().getRegistryManager());
        }
        if (blockEntity2 != null) {
            blockEntity2Nbt = blockEntity2.createNbt(ctx.getWorld().getRegistryManager());
            blockEntity2.read(new NbtCompound(), ctx.getWorld().getRegistryManager());
        }

        ctx.getWorld().setBlockState(blockPos1, state2);
        ctx.getWorld().setBlockState(blockPos2, state1);
        ctx.setWorldAffected();

        if (blockEntity1Nbt != null) {
            ctx.getWorld().getBlockEntity(blockPos2).read(blockEntity1Nbt, ctx.getWorld().getRegistryManager());
        }
        if (blockEntity2Nbt != null) {
            ctx.getWorld().getBlockEntity(blockPos1).read(blockEntity2Nbt, ctx.getWorld().getRegistryManager());
        }

        var particlePos1 = blockPos1.toCenterPos();
        var particlePos2 = blockPos2.toCenterPos();
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos1.x, particlePos1.y, particlePos1.z,
                1, 0, 0, 0, 0
        );
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos2.x, particlePos2.y, particlePos2.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}

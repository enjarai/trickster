package dev.enjarai.trickster.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SpellControlledRedstoneBlock {
    boolean setPower(World world, BlockPos pos, int power);

    int getPower(World world, BlockPos pos);
}

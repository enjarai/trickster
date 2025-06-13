package dev.enjarai.trickster.item;

import dev.enjarai.trickster.spell.SpellExecutor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class SpellCoreItem extends Item {
    public SpellCoreItem() {
        super(new Settings().maxCount(1));
    }

    //TODO: add javadocs
    public int getExecutionLimit(ServerWorld world, Vec3d pos, int originalExecutionLimit) {
        return originalExecutionLimit - originalExecutionLimit / 4;
    }

    //TODO: add javadocs
    public boolean onRemoved(ServerWorld world, BlockPos pos, ItemStack stack, Optional<SpellExecutor> executor) {
        return false;
    }

    public void onDisplayTick(World world, Vec3d pos, Random random) {

    }
}

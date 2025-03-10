package dev.enjarai.trickster.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class Trolling {
    public static void clearBlockEntityForDeletion(World world, BlockEntity entity) {
        entity.read(new NbtCompound(), world.getRegistryManager());
        if (entity instanceof LootableContainerBlockEntity lootable) {
            lootable.setLootTable(null);
        }
    }
}

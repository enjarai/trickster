package dev.enjarai.trickster.item;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class OminousSpellCoreItem extends SpellCoreItem {
    @Override
    public int getExecutionLimit(ServerWorld world, Vec3d pos, int originalExecutionLimit) {
        return originalExecutionLimit;
    }
}

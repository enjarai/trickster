package dev.enjarai.trickster.item;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SpawnerSpellCoreItem extends SpellCoreItem {
    @Override
    public int getExecutionLimit(ServerWorld world, Vec3d pos, int originalExecutionLimit) {
        if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)) {
            return originalExecutionLimit + originalExecutionLimit / 2;
        } else {
            return 0;
        }
    }

    @Override
    public void onDisplayTick(World world, Vec3d pos, Random random) {
        if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)) {
            for (int i = 0; i < 6; i++) {
                double d = pos.getX() + random.nextDouble() - 0.5;
                double e = pos.getY() + random.nextDouble() - 0.5;
                double f = pos.getZ() + random.nextDouble() - 0.5;
                world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }
}

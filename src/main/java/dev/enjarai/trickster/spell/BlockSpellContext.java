package dev.enjarai.trickster.spell;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

public class BlockSpellContext extends SpellContext {
    public final ServerWorld world;
    public final BlockPos pos;

    public BlockSpellContext(ServerWorld world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }
}

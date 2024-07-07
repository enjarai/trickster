package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

public class BlockSpellContext extends SpellContext {
    public final ServerWorld world;
    public final BlockPos pos;
    public final SpellCircleBlockEntity blockEntity;

    public BlockSpellContext(ServerWorld world, BlockPos pos, SpellCircleBlockEntity blockEntity) {
        super(blockEntity.manaPool, 0);
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }

    @Override
    public Fragment getCrowMind() {
        return blockEntity.crowMind.fragment();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        blockEntity.crowMind = new CrowMind(fragment);
        blockEntity.markDirty();
    }

    @Override
    public void addManaLink(Trick source, LivingEntity target, float limit) {
        addManaLink(source, new ManaLink(manaPool, target, manaPool.getMax() / 20, limit));
    }
}

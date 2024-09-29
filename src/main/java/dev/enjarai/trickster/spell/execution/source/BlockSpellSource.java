package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.ManaPool;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.Optional;

public class BlockSpellSource implements SpellSource {
    public final ServerWorld world;
    public final BlockPos pos;
    public final SpellCircleBlockEntity blockEntity;

    public BlockSpellSource(ServerWorld world, BlockPos pos, SpellCircleBlockEntity blockEntity) {
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
    }

    @Override
    public <T extends Component> Optional<T> getComponent(ComponentKey<T> key) {
        return key.maybeGet(blockEntity);
    }

    @Override
    public float getHealth() {
        return -1;
    }

    @Override
    public float getMaxHealth() {
        return -1;
    }

    @Override
    public ManaPool getManaPool() {
        return blockEntity.manaPool;
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public BlockPos getBlockPos() {
        return pos;
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
}

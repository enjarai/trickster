package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.mana.CachedInventoryManaPool;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.mana.generation.InventoryBlockManaHandler;
import dev.enjarai.trickster.spell.mana.generation.ManaHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.Optional;

public class BlockSpellSource<T extends BlockEntity & Inventory & CrowMind> implements SpellSource {
    public final ServerWorld world;
    public final BlockPos pos;
    public final T blockEntity;
    public final CachedInventoryManaPool pool;

    public BlockSpellSource(ServerWorld world, BlockPos pos, T blockEntity) {
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
        this.pool = new CachedInventoryManaPool(blockEntity);
    }

    @Override
    public <C extends Component> Optional<C> getComponent(ComponentKey<C> key) {
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
    public MutableManaPool getManaPool() {
        return pool;
    }

    @Override
    public Vector3d getPos() {
        var pos = this.pos.toCenterPos();
        return new Vector3d(pos.x, pos.y, pos.z);
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
        return blockEntity.getCrowMind();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        blockEntity.setCrowMind(fragment);
    }

    @Override
    public Optional<SpellExecutionManager> getExecutionManager() {
        if (blockEntity instanceof SpellExecutionManager manager)
            return Optional.of(manager);

        return Optional.empty();
    }

    @Override
    public ManaHandler getManaHandler() {
        return new InventoryBlockManaHandler(pos);
    }

    @Override
    public void offerOrDropItem(ItemStack stack) {
        var pos = getPos();
        world.spawnEntity(new ItemEntity(world, pos.x, pos.y, pos.z, stack));
    }
}

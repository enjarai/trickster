package dev.enjarai.trickster.spell.mana.generation;

import dev.enjarai.trickster.spell.mana.CachedInventoryManaPool;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class InventoryBlockManaHandler implements ManaHandler {
    public static final StructEndec<InventoryBlockManaHandler> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.BLOCK_POS.fieldOf("pos", handler -> handler.pos),
            InventoryBlockManaHandler::new
    );

    private BlockPos pos;

    public InventoryBlockManaHandler(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public ManaHandlerType<?> type() {
        return ManaHandlerType.INVENTORY_BLOCK;
    }

    @Override
    public float handleRefill(ServerWorld world, float amount) {
        var blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof Inventory inventory) {
            return new CachedInventoryManaPool(inventory).refill(amount);
        }

        return amount;
    }
}

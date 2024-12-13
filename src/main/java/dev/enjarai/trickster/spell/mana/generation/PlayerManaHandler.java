package dev.enjarai.trickster.spell.mana.generation;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.CachedInventoryManaPool;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerManaHandler implements ManaHandler {
    public static final StructEndec<PlayerManaHandler> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", handler -> handler.uuid),
            PlayerManaHandler::new
    );

    private final UUID uuid;

    private PlayerManaHandler(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerManaHandler(ServerPlayerEntity player) {
        this(player.getUuid());
    }

    @Override
    public ManaHandlerType<?> type() {
        return ManaHandlerType.PLAYER;
    }

    @Override
    public float handleRefill(ServerWorld world, float amount) {
        var entity = world.getEntity(uuid);

        if (entity instanceof ServerPlayerEntity player) {
            var pool = new CachedInventoryManaPool(player.getInventory());
            return pool.refill(amount, world);
        }

        return amount;
    }
}

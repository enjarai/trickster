package dev.enjarai.trickster.spell.mana.generation.event;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.mana.generation.HasMana;
import dev.enjarai.trickster.spell.mana.generation.ManaHandler;
import dev.enjarai.trickster.spell.mana.generation.ManaHandlerType;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.event.Level;

import java.util.UUID;

public record EntityManaHandler(UUID uuid) implements ManaHandler {
    public static final StructEndec<EntityManaHandler> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", EntityManaHandler::uuid),
            EntityManaHandler::new
    );

    public EntityManaHandler(Entity entity) {
        this(entity.getUuid());
        if (!(entity instanceof HasMana)) {
            throw new IllegalStateException(String.format("a mana handler was made from an entity (%s) that does not have mana", entity));
            // throw new Trickster.LOGGER.warn("a mana handler was made from an entity {} that does not have mana", entity);
        }
    }

    @Override
    public ManaHandlerType<?> type() {
        return ManaHandlerType.ENTITY;
    }

    @Override
    public float handleRefill(ServerWorld world, float amount) {
        //since we throw above... this *should* be fine
        //noinspection unchecked
        var entity = (HasMana) world.getEntity(uuid);
        if (entity == null) {
            return amount;
        }
        var mutableManaPool= entity.getPool();
        return mutableManaPool.refill(amount, world);
    }

}

package dev.enjarai.trickster.spell.mana;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public record SharedManaPool(UUID uuid) implements MutableManaPool {
    public static final StructEndec<SharedManaPool> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", SharedManaPool::uuid),
            SharedManaPool::new);

    private static final SimpleManaPool THE_SKILL_ISSUE = new SimpleManaPool(0) {
        @Override
        public void set(float value, World world) {
            mana = 0;
        }

        @Override
        public void setMax(float value, World world) {
            maxMana = 0;
        }
    };

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SHARED;
    }

    @Override
    public float get(World world) {
        return getSelf(world).get(world);
    }

    @Override
    public float getMax(World world) {
        return getSelf(world).getMax(world);
    }

    @Override
    public void set(float value, World world) {
        getSelf(world).set(value, world);
        ModGlobalComponents.SHARED_MANA.sync(world.getScoreboard());
    }

    @Override
    public void setMax(float value, World world) {
        getSelf(world).setMax(value, world);
        ModGlobalComponents.SHARED_MANA.sync(world.getScoreboard());
    }

    @Override
    public float use(float amount, World world) {
        var result = getSelf(world).use(amount, world);
        ModGlobalComponents.SHARED_MANA.sync(world.getScoreboard());
        return result;
    }

    @Override
    public float refill(float amount, World world) {
        var result = getSelf(world).refill(amount, world);
        ModGlobalComponents.SHARED_MANA.sync(world.getScoreboard());
        return result;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SharedManaPool(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SharedManaPool pool && uuid.equals(pool.uuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    private SimpleManaPool getSelf(World world) {
        return ModGlobalComponents.SHARED_MANA.get(world.getScoreboard()).get(uuid).orElse(THE_SKILL_ISSUE);
    }
}

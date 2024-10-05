package dev.enjarai.trickster.spell.mana;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record SharedManaPool(UUID uuid) implements MutableManaPool {
    public static final StructEndec<SharedManaPool> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", SharedManaPool::uuid),
            SharedManaPool::new
    );

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SHARED;
    }

    @Override
    public float get() {
        return SharedManaComponent.INSTANCE.get(uuid).map(ManaPool::get).orElse(0f);
    }

    @Override
    public float getMax() {
        return SharedManaComponent.INSTANCE.get(uuid).map(ManaPool::getMax).orElse(0f);
    }

    @Override
    public void set(float value) {
        SharedManaComponent.INSTANCE.get(uuid).ifPresent(pool -> pool.set(value));
    }

    @Override
    public void setMax(float value) {
        SharedManaComponent.INSTANCE.get(uuid).ifPresent(pool -> pool.setMax(value));
    }

    @Override
    public float use(float amount) {
        return SharedManaComponent.INSTANCE.get(uuid).map(pool -> pool.use(amount)).orElse(amount);
    }

    @Override
    public float refill(float amount) {
        return SharedManaComponent.INSTANCE.get(uuid).map(pool -> pool.refill(amount)).orElse(amount);
    }

    @Override
    public MutableManaPool makeClone() {
        return new SharedManaPool(uuid);
    }
}

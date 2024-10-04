package dev.enjarai.trickster.spell.mana;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class SharedManaPool implements MutableManaPool {
    public static final StructEndec<SharedManaPool> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", s -> s.uuid),
            SharedManaPool::new
    );

    private UUID uuid;

    public SharedManaPool(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SHARED;
    }

    @Override
    public float get() {
        return SharedManaComponent.INSTANCE.get(uuid).get();
    }

    @Override
    public float getMax() {
        return SharedManaComponent.INSTANCE.get(uuid).getMax();
    }

    @Override
    public void set(float value) {
        SharedManaComponent.INSTANCE.get(uuid).set(value);
    }

    @Override
    public void setMax(float value) {
        SharedManaComponent.INSTANCE.get(uuid).setMax(value);
    }

    @Override
    public float use(float amount) {
        return SharedManaComponent.INSTANCE.get(uuid).use(amount);
    }

    @Override
    public float refill(float amount) {
        return SharedManaComponent.INSTANCE.get(uuid).refill(amount);
    }

    @Override
    public MutableManaPool makeClone() {
        return new SharedManaPool(uuid);
    }
}

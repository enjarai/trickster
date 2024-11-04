package dev.enjarai.trickster.spell.mana;

import java.util.Optional;
import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.SharedManaComponent;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class SharedManaPool implements MutableManaPool {
    public static final StructEndec<SharedManaPool> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", SharedManaPool::uuid),
            SharedManaPool::new
    );

    private final UUID uuid;
    private Optional<SimpleManaPool> self = Optional.empty();

    public SharedManaPool(UUID uuid) {
        this.uuid = uuid;
    }

    private SharedManaPool(UUID uuid, Optional<SimpleManaPool> self) {
        this(uuid);
        this.self = self;
    }

    public UUID uuid() {
        return uuid;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SHARED;
    }

    @Override
    public float get() {
        return getSelf().map(SimpleManaPool::get).orElse(0f);
    }

    @Override
    public float getMax() {
        return getSelf().map(SimpleManaPool::get).orElse(0f);
    }

    @Override
    public void set(float value) {
        getSelf().ifPresent(pool -> pool.set(value));
    }

    @Override
    public void setMax(float value) {
        getSelf().ifPresent(pool -> pool.setMax(value));
    }

    @Override
    public float use(float amount) {
        return getSelf().map(pool -> pool.use(amount)).orElse(amount);
    }

    @Override
    public float refill(float amount) {
        return getSelf().map(pool -> pool.refill(amount)).orElse(amount);
    }

    @Override
    public MutableManaPool makeClone() {
        return new SharedManaPool(uuid, self);
    }

    private Optional<SimpleManaPool> getSelf() {
        return self = self.or(() -> SharedManaComponent.INSTANCE.get(uuid));
    }
}

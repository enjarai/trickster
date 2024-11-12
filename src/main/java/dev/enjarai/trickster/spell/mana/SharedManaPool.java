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

    private static final SimpleManaPool THE_SKILL_ISSUE = new SimpleManaPool(0) {
        @Override
        public void set(float value) {
            mana = 0;
        }

        @Override
        public void setMax(float value) {
            maxMana = 0;
        }
    };

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
        return getSelf().get();
    }

    @Override
    public float getMax() {
        return getSelf().getMax();
    }

    @Override
    public void set(float value) {
        getSelf().set(value);
    }

    @Override
    public void setMax(float value) {
        getSelf().setMax(value);
    }

    @Override
    public float use(float amount) {
        return getSelf().use(amount);
    }

    @Override
    public float refill(float amount) {
        return getSelf().refill(amount);
    }

    @Override
    public MutableManaPool makeClone() {
        return new SharedManaPool(uuid, self);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SharedManaPool pool && uuid.equals(pool.uuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    private SimpleManaPool getSelf() {
        return (self = self.or(() -> SharedManaComponent.getInstance().get(uuid))).orElse(THE_SKILL_ISSUE);
    }
}

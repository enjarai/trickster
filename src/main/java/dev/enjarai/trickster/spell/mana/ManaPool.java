package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import org.jetbrains.annotations.Nullable;

public interface ManaPool {
    @SuppressWarnings("unchecked")
    StructEndec<ManaPool> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<ManaPool>) Endec.dispatchedStruct(ManaPoolType::endec, pool -> {
        var type = pool.type();

        if (type == null) {
            throw new UnsupportedOperationException("This mana pool type cannot be serialized");
        }

        return type;
    }, MinecraftEndecs.ofRegistry(ManaPoolType.REGISTRY)));

    static float healthFromMana(float mana) {
        return mana / 2;
    }

    static float manaFromHealth(float health) {
        return health * 12;
    }

    @Nullable
    ManaPoolType<?> type();

    void set(float value);

    float get();

    float getMax();

    default void increase(float amount) {
        if (Float.isNaN(get()))
            set(0);

        set(get() + amount);
    }

    /**
     * Returns whether the pool still has mana.
     */
    default boolean decrease(float amount) {
        if (Float.isNaN(get()))
            set(0);

        float f = get() - amount;
        set(f);
        return !(f < 0);
    }
}

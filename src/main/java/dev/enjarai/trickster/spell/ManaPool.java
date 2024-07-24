package dev.enjarai.trickster.spell;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;

public interface ManaPool {
    Supplier<MapCodec<ManaPool>> CODEC = Suppliers.memoize(() -> ManaPoolType.REGISTRY.getCodec().dispatchMap(ManaPool::type, ManaPoolType::codec));

    static float healthFromMana(float mana) {
        return mana / 2;
    }

    static float manaFromHealth(float health) {
        return health * 12;
    }

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

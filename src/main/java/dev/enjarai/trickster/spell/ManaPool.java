package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;

public interface ManaPool {
    static float healthFromMana(float mana) {
        return mana / 2;
    }

    static float manaFromHealth(float health) {
        return health * 12;
    }

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

    Codec<? extends ManaPool> getCodec();

}

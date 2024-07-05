package dev.enjarai.trickster.spell;

public interface ManaPool {
    float FACTOR = 25;

    static float healthFromMana(float mana) {
        return mana / FACTOR;
    }

    static float manaFromHealth(float health) {
        return health * FACTOR;
    }

    void set(float value);

    float get();

    float getMax();

    default void increase(float amount) {
        set(get() + amount);
    }

    /**
     * Returns whether the pool still has mana.
     */
    default boolean decrease(float amount) {
        float f = get() - amount;
        set(f);
        return !(f < 0);
    }
}

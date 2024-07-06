package dev.enjarai.trickster.spell;

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

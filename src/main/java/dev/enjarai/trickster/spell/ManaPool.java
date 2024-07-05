package dev.enjarai.trickster.spell;

public class ManaPool {
    public static float FACTOR = 25;

    protected float maxMana = 450;
    protected float mana;

    protected void set(float value) {
        mana = Math.max(Math.min(value, maxMana), 0);
    }

    public float get() {
        return mana;
    }

    public float getMax() {
        return maxMana;
    }

    public void increase(float amount) {
        set(mana + amount);
    }

    /**
     * Returns whether the pool still has mana.
     */
    public boolean decrease(float amount) {
        float f = mana - amount;
        set(mana - amount);
        return !(f < 0);
    }

    protected void stdIncrease() {
        increase(maxMana / 4000);
    }

    public static float healthFromMana(float mana) {
        return mana / FACTOR;
    }

    public static float manaFromHealth(float health) {
        return health * FACTOR;
    }
}

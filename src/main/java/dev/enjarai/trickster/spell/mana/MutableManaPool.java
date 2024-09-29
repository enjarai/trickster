package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;

public interface MutableManaPool extends ManaPool {
    public static final Endec<MutableManaPool> ENDEC = ManaPool.ENDEC.xmap(imp -> (MutableManaPool) imp, mp -> mp);

    void set(float current);

    void setMax(float max);

    /**
     * @param amount the amount to consume from this pool.
     * @return the amount not consumed from this pool.
     */
    default float use(float amount) {
        if (get() >= amount) {
            set(get() - amount);
            return 0;
        } else {
            var result = amount - get();
            set(0);
            return result;
        }
    }

    /**
     * @param amount the amount to refill this pool with.
     * @return the amount that could not be added to this pool.
     */
    default float refill(float amount) {
        if (getMax() - get() >= amount) {
            set(get() + amount);
            return 0;
        } else {
            var result = amount - (getMax() - get());
            set(getMax());
            return result;
        }
    }
}

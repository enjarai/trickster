package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.cca.ManaComponent;

public class ManaLink {
    public static float FACTOR = 1.7f;

    public final ManaComponent manaPool;
    private float availableMana;

    public ManaLink(ManaComponent manaPool, float availableMana) {
        this.manaPool = manaPool;
        this.availableMana = availableMana;
    }

    public float useMana(float amount) {
        float oldMana = manaPool.get();
        float result = getAvailable();

        if (amount > getAvailable()) {

            if (!manaPool.decrease(availableMana))
                availableMana -= oldMana;
            else
                availableMana = 0;
        } else {
            float postAmount = amount * FACTOR;

            if (!manaPool.decrease(postAmount))
                availableMana -= oldMana;
            else
                availableMana -= postAmount;
        }

        return result - getAvailable();
    }

    public float getAvailable() {
        return availableMana / FACTOR;
    }
}

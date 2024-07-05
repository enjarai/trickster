package dev.enjarai.trickster.spell;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class SimpleManaPool implements ManaPool {
    public static final Endec<ManaPool> ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("mana", ManaPool::get),
            Endec.FLOAT.fieldOf("max_mana", ManaPool::getMax),
            SimpleManaPool::new
    );

    protected float maxMana;
    protected float mana;

    public SimpleManaPool(float maxMana) {
        this.maxMana = maxMana;
    }

    public SimpleManaPool(float mana, float maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    @Override
    public void set(float value) {
        mana = Math.max(Math.min(value, maxMana), 0);
    }

    @Override
    public float get() {
        return mana;
    }

    @Override
    public float getMax() {
        return maxMana;
    }

    protected void stdIncrease() {
        increase(maxMana / 4000);
    }

    public static SimpleManaPool getSingleUse(float mana) {
        return new SimpleManaPool(mana, mana);
    }
}

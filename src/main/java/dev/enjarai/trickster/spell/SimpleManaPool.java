package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class SimpleManaPool implements ManaPool {
    public static final Endec<SimpleManaPool> ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("mana", ManaPool::get),
            Endec.FLOAT.fieldOf("max_mana", ManaPool::getMax),
            SimpleManaPool::new
    );

    public static final Codec<SimpleManaPool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mana").forGetter(SimpleManaPool::get),
            Codec.FLOAT.fieldOf("max_mana").forGetter(SimpleManaPool::getMax)
    ).apply(instance, SimpleManaPool::new));

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
        mana = Float.isNaN(mana) ? 0 : Math.max(Math.min(value, maxMana), 0);
    }

    @Override
    public float get() {
        return Float.isNaN(mana) ? 0 : mana;
    }

    @Override
    public float getMax() {
        return maxMana;
    }

    @Override
    public Codec<? extends ManaPool> getCodec() {
        return CODEC;
    }

    public void stdIncrease() {
        stdIncrease(1);
    }

    public void stdIncrease(float multiplier) {
        increase((maxMana / 4000) * multiplier);
    }

    public static SimpleManaPool getSingleUse(float mana) {
        return new SimpleManaPool(mana, mana);
    }
}

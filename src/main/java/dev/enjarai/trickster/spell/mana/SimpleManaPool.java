package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SimpleManaPool implements MutableManaPool {
    public static final StructEndec<SimpleManaPool> ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("mana", simpleManaPool -> simpleManaPool.mana),
            Endec.FLOAT.fieldOf("max_mana", simpleManaPool -> simpleManaPool.maxMana),
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
    public ManaPoolType<?> type() {
        return ManaPoolType.SIMPLE;
    }

    @Override
    public void set(float value, World world) {
        mana = Math.clamp(value, 0, maxMana);
    }

    @Override
    public float get(World world) {
        return mana;
    }

    @Override
    public void setMax(float value, World world) {
        maxMana = value;
    }

    @Override
    public float getMax(World world) {
        return maxMana;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SimpleManaPool(mana, maxMana);
    }

    public static SimpleManaPool getSingleUse(float mana) {
        return new SimpleManaPool(mana, mana);
    }
}

package dev.enjarai.trickster.spell.mana;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.StructEndec;

public class InfiniteManaPool implements MutableManaPool {
    public static final InfiniteManaPool INSTANCE = new InfiniteManaPool();
    public static final StructEndec<InfiniteManaPool> ENDEC = EndecTomfoolery.unit(INSTANCE);

    private InfiniteManaPool() {
    }
    
    @Override
    @Nullable
    public ManaPoolType<?> type() {
        return ManaPoolType.INFINITE;
    }

    @Override
    public float get() {
        return Float.MAX_VALUE;
    }

    @Override
    public float getMax() {
        return Float.MAX_VALUE;
    }

    @Override
    public void set(float current) {
    }

    @Override
    public void setMax(float max) {
    }

    @Override
    public MutableManaPool makeClone() throws UnsupportedOperationException {
        return this;
    }
}

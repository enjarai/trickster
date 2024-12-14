package dev.enjarai.trickster.spell.mana;

import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.StructEndec;

public final class InfiniteManaPool implements MutableManaPool {
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
    public float get(World world) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getMax(World world) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public void set(float current, World world) {
    }

    @Override
    public void setMax(float max, World world) {
    }

    @Override
    public MutableManaPool makeClone(World world) throws UnsupportedOperationException {
        return INSTANCE;
    }
}

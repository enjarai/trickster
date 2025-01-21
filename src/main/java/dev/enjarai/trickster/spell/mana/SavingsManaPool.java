package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SavingsManaPool extends SimpleManaPool {
    public static final StructEndec<SavingsManaPool> ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("mana", pool -> pool.mana),
            Endec.FLOAT.fieldOf("max_mana", pool -> pool.maxMana),
            Endec.FLOAT.fieldOf("interest", pool -> pool.interest),
            Endec.LONG.fieldOf("last_update_time", pool -> pool.lastUpdateTime),
            SavingsManaPool::new
    );

    protected final float interest;
    protected long lastUpdateTime;

    protected SavingsManaPool(float mana, float maxMana, float interest, long lastUpdateTime) {
        super(mana, maxMana);
        this.interest = interest;
        this.lastUpdateTime = lastUpdateTime;
    }

    // It's kinda important for this to be the only public constructor.
    // If lastUpdateTime is 0, mana should probably also be 0, or a lot of free interest will be given.
    public SavingsManaPool(float maxMana, float interest) {
        super(maxMana);
        this.interest = interest;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SAVINGS;
    }

    @Override
    public void set(float value, World world) {
        lastUpdateTime = world.getTime();
        super.set(value, world);
    }

    @Override
    public float get(World world) {
        var ticksPassed = world.getTime() - lastUpdateTime; // t
        var lastMana = super.get(world); // P

        // P(1 + i)^t
        set((float) Math.clamp(lastMana * Math.pow(1 + interest, ticksPassed), 0, getMax(world)), world);
        return super.get(world);
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SavingsManaPool(mana, maxMana, interest, lastUpdateTime);
    }
}

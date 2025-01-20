package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SavingsManaPool extends SimpleManaPool {
    public static final StructEndec<SavingsManaPool> ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("mana", pool -> pool.mana),
            Endec.FLOAT.fieldOf("max_mana", pool -> pool.maxMana),
            Endec.LONG.fieldOf("last_update_time", pool -> pool.lastUpdateTime),
            Endec.FLOAT.fieldOf("interest", pool -> pool.interest),
            SavingsManaPool::new
    );

    protected long lastUpdateTime;
    protected float interest;

    protected SavingsManaPool(float mana, float maxMana, long lastUpdateTime, float interest) {
        super(mana, maxMana);
        this.lastUpdateTime = lastUpdateTime;
        this.interest = interest;
    }

    // Its kinda important for this to be the only public constructor.
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
        var ticksPassed = world.getTime() - lastUpdateTime;
        var lastMana = super.get(world);

        return (float) Math.clamp(lastMana * Math.pow(1 + interest, ticksPassed), 0, getMax(world));
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SavingsManaPool(mana, maxMana, lastUpdateTime, interest);
    }
}

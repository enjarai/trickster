package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;
import net.minecraft.world.World;

public interface MutableManaPool extends ManaPool {
    Endec<MutableManaPool> ENDEC = ManaPool.ENDEC.xmap(imp -> (MutableManaPool) imp, mp -> mp);

    void set(float current, World world);

    void setMax(float max, World world);

    /**
     * @param amount
     *               the amount to consume from this pool.
     * @param world
     * @return the amount not consumed from this pool.
     */
    default float use(float amount, World world) {
        if (get(world) >= amount) {
            set(get(world) - amount, world);
            return 0;
        } else {
            var result = amount - get(world);
            set(0, world);
            return result;
        }
    }

    /**
     * @param amount
     *               the amount to refill this pool with.
     * @param world
     * @return the amount that could not be added to this pool.
     */
    default float refill(float amount, World world) {
        if (getMax(world) - get(world) >= amount) {
            set(get(world) + amount, world);
            return 0;
        } else {
            var result = amount - (getMax(world) - get(world));
            set(getMax(world), world);
            return result;
        }
    }
}

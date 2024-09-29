package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import org.jetbrains.annotations.Nullable;

public interface ImmutableManaPool {
    @SuppressWarnings("unchecked")
    StructEndec<ImmutableManaPool> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<ImmutableManaPool>) Endec.dispatchedStruct(ManaPoolType::endec, pool -> {
        var type = pool.type();

        if (type == null) {
            throw new UnsupportedOperationException("This mana pool type cannot be serialized");
        }

        return type;
    }, MinecraftEndecs.ofRegistry(ManaPoolType.REGISTRY)));

    @Nullable
    ManaPoolType<?> type();

    float get();

    float getMax();

    /**
     * Overwrites `result` with this pool's current and maximum, then applies this operation using `result`'s implementation.
     * @param amount the amount to consume from this pool.
     * @param result the ManaPool to overwrite with the result of this operation.
     * @return the amount not consumed from this pool.
     */
    default float use(float amount, ManaPool result) {
        result.setMax(getMax());
        result.set(get());
        return result.use(amount);
    }

    /**
     * Overwrites `result` with this pool's current and maximum, then applies this operation using `result`'s implementation.
     * @param amount the amount to refill this pool with.
     * @param result the ManaPool to overwrite with the result of this operation.
     * @return the amount that could not be added to this pool.
     */
    default float refill(float amount, ManaPool result) {
        result.setMax(getMax());
        result.set(get());
        return result.refill(amount);
    }
}

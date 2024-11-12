package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import org.jetbrains.annotations.Nullable;

public interface ManaPool {
    @SuppressWarnings("unchecked")
    StructEndec<ManaPool> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<ManaPool>) Endec.dispatchedStruct(ManaPoolType::endec, pool -> {
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

    MutableManaPool makeClone() throws UnsupportedOperationException;
}

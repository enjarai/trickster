package dev.enjarai.trickster.fleck;

import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

public interface Fleck {
    Endec<Fleck> ENDEC = Endec.dispatchedStruct(
            FleckType::endec,
            Fleck::type,
            MinecraftEndecs.ofRegistry(FleckType.REGISTRY)
    );

    FleckType<?> type();
}


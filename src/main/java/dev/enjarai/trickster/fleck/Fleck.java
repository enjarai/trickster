package dev.enjarai.trickster.fleck;

import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

public interface Fleck {
    public static final Endec<Fleck> ENDEC = Endec.dispatchedStruct(
            FleckType::endec,
            Fleck::type,
            MinecraftEndecs.ofRegistry(FleckType.REGISTRY )
    );

    FleckType<?> type();

}


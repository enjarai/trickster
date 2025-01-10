package dev.enjarai.trickster.spell.blot;

import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

public interface Blot {
    Endec<Blot> ENDEC = Endec.dispatchedStruct(
            BlotType::endec,
            Blot::type,
            MinecraftEndecs.ofRegistry(BlotType.REGISTRY)
    );

    BlotType<?> type();
}

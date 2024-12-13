package dev.enjarai.trickster.spell.mana.generation.event;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

public interface ManaEvent {
    @SuppressWarnings("unchecked")
    StructEndec<ManaEvent> ENDEC = EndecTomfoolery.lazy(
            () -> (StructEndec<ManaEvent>) Endec.dispatchedStruct(ManaEventType::endec, ManaEvent::type, MinecraftEndecs.ofRegistry(ManaEventType.REGISTRY)));

    ManaEventType<?> type();

    boolean fulfilledBy(ManaEvent event);

    boolean detachedBy(ManaEvent event);

    float getMana();
}

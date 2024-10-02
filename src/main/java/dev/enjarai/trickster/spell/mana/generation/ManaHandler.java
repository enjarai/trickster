package dev.enjarai.trickster.spell.mana.generation;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.server.world.ServerWorld;

public interface ManaHandler {
    @SuppressWarnings("unchecked")
    StructEndec<ManaHandler> ENDEC = EndecTomfoolery.lazy(() -> (StructEndec<ManaHandler>) Endec.dispatchedStruct(ManaHandlerType::endec, ManaHandler::type, MinecraftEndecs.ofRegistry(ManaHandlerType.REGISTRY)));

    ManaHandlerType<?> type();

    float handleRefill(ServerWorld world, float amount);
}

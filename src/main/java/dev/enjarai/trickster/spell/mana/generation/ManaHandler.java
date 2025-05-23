package dev.enjarai.trickster.spell.mana.generation;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.server.world.ServerWorld;

//TODO: these should be easily comparable (records) so that we can avoid duplicates in the world mana handler
public interface ManaHandler {
    @SuppressWarnings("unchecked")
    StructEndec<ManaHandler> ENDEC = EndecTomfoolery
            .lazyStruct(() -> (StructEndec<ManaHandler>) Endec.dispatchedStruct(ManaHandlerType::endec, ManaHandler::type, MinecraftEndecs.ofRegistry(ManaHandlerType.REGISTRY)));

    ManaHandlerType<?> type();

    float handleRefill(ServerWorld world, float amount);
}

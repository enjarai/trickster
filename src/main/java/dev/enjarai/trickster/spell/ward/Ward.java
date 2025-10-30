package dev.enjarai.trickster.spell.ward;

import org.joml.Vector3dc;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.server.world.ServerWorld;

public interface Ward {
    @SuppressWarnings("unchecked")
    StructEndec<Ward> ENDEC = EndecTomfoolery.lazyStruct(() -> (StructEndec<Ward>) Endec.dispatchedStruct(WardType::endec, Ward::type, MinecraftEndecs.ofRegistry(WardType.REGISTRY)));

    WardType<?> type();

    void tick(ServerWorld world);

    boolean shouldLive(ServerWorld world);

    boolean matchPos(Vector3dc pos);
}

package dev.enjarai.trickster.spell.ward;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.ward.action.ActionType;
import dev.enjarai.trickster.spell.ward.action.Target;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.world.World;

public interface Ward {
    @SuppressWarnings("unchecked")
    StructEndec<Ward> ENDEC = EndecTomfoolery.lazyStruct(
            () -> (StructEndec<Ward>) Endec.dispatchedStruct(
                    WardType::endec, Ward::type, MinecraftEndecs.ofRegistry(WardType.REGISTRY)
            )
    );

    WardType<?> type();

    void tick(World world);

    void drain(World world, float amount);

    boolean shouldLive(World world);

    boolean matchTarget(Target target);

    boolean matchAction(ActionType<?> action);
}

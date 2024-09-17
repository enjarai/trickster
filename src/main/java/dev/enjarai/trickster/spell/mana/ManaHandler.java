package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;

import java.util.List;

public interface ManaHandler {
    @SuppressWarnings("unchecked")
    StructEndec<ManaHandler> ENDEC = EndecTomfoolery.lazy(() ->
            (StructEndec<ManaHandler>) Endec.dispatchedStruct(ManaHandlerType::endec, handler ->
                    handler.type(), MinecraftEndecs.ofRegistry(ManaHandlerType.REGISTRY)));

    ManaHandlerType<?> type();

    void handleUseMana(Trick trickSource, SpellContext ctx, ManaPool pool, List<ManaLink> manaLinks, float amount) throws BlunderException;
}

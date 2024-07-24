package dev.enjarai.trickster.spell;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public sealed interface SpellInstruction permits Fragment, EnterScopeInstruction, ExitScopeInstruction {
    Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator();

    SerializedSpellInstruction asSerialized();

}

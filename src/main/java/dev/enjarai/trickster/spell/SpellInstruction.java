package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public sealed interface SpellInstruction permits Fragment, EnterScopeInstruction, ExitScopeInstruction {
    SerializedSpellInstruction asSerialized();

    default Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator() {
        return Optional.empty();
    }

    default SpellQueue makeFork(SpellContext ctx, List<Fragment> args) throws BlunderException {
        throw new NotImplementedException();
    }

    default boolean forks() {
        return false;
    }
}

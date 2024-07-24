package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public sealed interface SpellInstruction permits Fragment, EnterScopeInstruction, ExitScopeInstruction {
    SerializedSpellInstruction asSerialized();

    default Optional<BiFunction<SpellSource, List<Fragment>, Fragment>> getActivator() {
        return Optional.empty();
    }

    default SpellExecutor makeFork(SpellContext ctx, List<Fragment> args) throws BlunderException {
        throw new NotImplementedException();
    }

    default boolean forks() {
        return false;
    }
}

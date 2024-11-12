package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import io.wispforest.endec.Endec;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;

public sealed interface SpellInstruction permits Fragment, EnterScopeInstruction, ExitScopeInstruction {
    public static final Endec<Stack<SpellInstruction>> STACK_ENDEC = SerializedSpellInstruction.ENDEC.listOf().xmap((l) -> {
        var s = new Stack<SpellInstruction>();
        s.addAll(l.stream().map(SerializedSpellInstruction::toDeserialized).toList());
        return s;
    }, (s) -> s.stream().map(SpellInstruction::asSerialized).toList());

    SerializedSpellInstruction asSerialized();

    default Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator() {
        return Optional.empty();
    }

    default SpellExecutor makeFork(SpellContext ctx, List<Fragment> args) throws BlunderException {
        throw new NotImplementedException();
    }

    default boolean forks(SpellContext ctx, List<Fragment> args) {
        return false;
    }
}

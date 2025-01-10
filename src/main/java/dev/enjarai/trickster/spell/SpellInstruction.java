package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import io.wispforest.endec.Endec;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;

public sealed interface SpellInstruction permits Fragment, EnterScopeInstruction, ExitScopeInstruction {
    public static final Endec<Stack<SpellInstruction>> STACK_ENDEC = SerializedSpellInstruction.ENDEC.listOf().xmap(l -> {
        var s = new Stack<SpellInstruction>();
        s.addAll(l.stream().map(SerializedSpellInstruction::toDeserialized).toList());
        return s;
    }, (s) -> s.stream().map(SpellInstruction::asSerialized).toList());

    SerializedSpellInstruction asSerialized();

    default Optional<BiFunction<SpellContext, List<Fragment>, EvaluationResult>> getActivator() {
        return Optional.empty();
    }
}

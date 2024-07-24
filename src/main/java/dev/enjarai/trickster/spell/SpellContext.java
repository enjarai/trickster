package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;

public record SpellContext(SpellSource source, ExecutionState executionState) {
}

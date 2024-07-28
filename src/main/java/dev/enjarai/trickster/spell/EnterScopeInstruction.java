package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellInstructionType;

public non-sealed class EnterScopeInstruction implements SpellInstruction {
    @Override
    public SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.ENTER_SCOPE, null);
    }
}

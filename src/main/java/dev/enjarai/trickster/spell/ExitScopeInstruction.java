package dev.enjarai.trickster.spell;

public non-sealed class ExitScopeInstruction implements SpellInstruction {
    @Override
    public SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.EXIT_SCOPE, null);
    }
}

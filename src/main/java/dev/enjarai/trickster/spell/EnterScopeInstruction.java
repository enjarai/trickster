package dev.enjarai.trickster.spell;

public non-sealed class EnterScopeInstruction implements SpellInstruction {
    @Override
    public SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.ENTER_SCOPE, null);
    }
}

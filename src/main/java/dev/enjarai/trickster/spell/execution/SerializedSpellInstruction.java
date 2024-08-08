package dev.enjarai.trickster.spell.execution;

import dev.enjarai.trickster.spell.EnterScopeInstruction;
import dev.enjarai.trickster.spell.ExitScopeInstruction;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellInstruction;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SerializedSpellInstruction(SpellInstructionType type, @Nullable Fragment fragment) {
    public static final StructEndec<SerializedSpellInstruction> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("instruction_id", s -> s.type.getId()),
            Fragment.ENDEC.optionalOf().fieldOf("fragment", s -> Optional.ofNullable(s.fragment)),
            (id, optionalFragment) -> new SerializedSpellInstruction(SpellInstructionType.fromId(id), optionalFragment.orElse(null))
    );

    public SpellInstruction toDeserialized() {
        return switch (type) {
            case FRAGMENT -> fragment;
            case ENTER_SCOPE -> new EnterScopeInstruction();
            case EXIT_SCOPE -> new ExitScopeInstruction();
        };

    }

}
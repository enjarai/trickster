package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.EnterScopeInstruction;
import dev.enjarai.trickster.spell.ExitScopeInstruction;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellInstruction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SerializedSpellInstruction(SpellInstructionType type, @Nullable Fragment fragment) {

    public static final Codec<SerializedSpellInstruction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("instruction_id").forGetter(s -> s.type.getId()),
            Fragment.CODEC.get().codec().optionalFieldOf("fragment").forGetter(s -> Optional.ofNullable(s.fragment))
    ).apply(instance, (id, optionalFragment) -> new SerializedSpellInstruction(SpellInstructionType.fromId(id), optionalFragment.orElse(null))));

    public SpellInstruction toDeserialized() {
        return switch (type) {
            case FRAGMENT -> fragment;
            case ENTER_SCOPE -> new EnterScopeInstruction();
            case EXIT_SCOPE -> new ExitScopeInstruction();
        };

    }

}
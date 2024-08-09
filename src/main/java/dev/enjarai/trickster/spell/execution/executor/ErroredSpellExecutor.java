package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record ErroredSpellExecutor(Text errorMessage) implements SpellExecutor {
    public static final StructEndec<ErroredSpellExecutor> ENDEC = StructEndecBuilder.of(
            CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC).fieldOf("error_message", e -> e.errorMessage),
            ErroredSpellExecutor::new
    );

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ERRORED;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, int executions) throws BlunderException {
        return Optional.empty();
    }

    @Override
    public int getLastRunExecutions() {
        return 0;
    }

    @Override
    public ExecutionState getCurrentState() {
        return new ExecutionState(List.of());
    }
}

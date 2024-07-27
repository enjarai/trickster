package dev.enjarai.trickster.spell.execution.executor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record ErroredSpellExecutor(Text errorMessage) implements SpellExecutor {
    public static final MapCodec<ErroredSpellExecutor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TextCodecs.CODEC.fieldOf("error_message").forGetter(e -> e.errorMessage)
    ).apply(instance, ErroredSpellExecutor::new));

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

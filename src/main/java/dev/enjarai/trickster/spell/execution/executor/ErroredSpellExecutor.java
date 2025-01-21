package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record ErroredSpellExecutor(SpellPart spell, Text errorMessage) implements SpellExecutor {
    public static final StructEndec<ErroredSpellExecutor> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", ErroredSpellExecutor::spell),
            CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC).fieldOf("error_message", ErroredSpellExecutor::errorMessage),
            ErroredSpellExecutor::new
    );

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.ERRORED;
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        return Optional.empty();
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx) throws BlunderException {
        return Optional.empty();
    }

    @Override
    public int getLastRunExecutions() {
        return 0;
    }

    @Override
    public ExecutionState getDeepestState() {
        return new ExecutionState(List.of());
    }
}

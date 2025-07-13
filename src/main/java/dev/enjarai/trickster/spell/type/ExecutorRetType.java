package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.SpellExecutor;
import net.minecraft.text.MutableText;

public record ExecutorRetType<T>(RetType<T> shadow) implements RetType<SpellExecutor> {
    @Override
    public MutableText asText() {
        return shadow.asText();
    }

    @Override
    public EvaluationResult into(SpellExecutor result) {
        return result;
    }
}

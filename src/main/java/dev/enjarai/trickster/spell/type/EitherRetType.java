package dev.enjarai.trickster.spell.type;

import dev.enjarai.trickster.spell.EvaluationResult;
import io.vavr.control.Either;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public record EitherRetType<T1, T2>(RetType<T1> left, RetType<T2> right) implements RetType<Either<T1, T2>> {
    @Override
    public MutableText asText() {
        return Text.empty().append(left.asText()).append(" | ").append(right.asText());
    }

    @Override
    public EvaluationResult into(Either<T1, T2> result) {
        return result.bimap(left::into, right::into).get();
    }
}

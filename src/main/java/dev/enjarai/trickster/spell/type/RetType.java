package dev.enjarai.trickster.spell.type;

import java.util.Optional;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface RetType<T> {
    static final RetType<Fragment> ANY = simple(Fragment.class);

    MutableText asText();

    EvaluationResult into(T result);

    default ExecutorRetType<T> executor() {
        return new ExecutorRetType<>(this);
    }

    default <O> EitherRetType<T, O> or(RetType<O> other) {
        return new EitherRetType<>(this, other);
    }

    default RetType<Optional<T>> maybe() {
        return new RetType<>() {
            @Override
            public MutableText asText() {
                return Text.empty().append(RetType.this.asText()).append(" | ").append(FragmentType.VOID.asText());
            }

            @Override
            public EvaluationResult into(Optional<T> result) {
                return result.map(r -> RetType.this.into(r)).orElse(VoidFragment.INSTANCE);
            }
        };
    }

    static <C extends Fragment> RetType<C> simple(Class<C> type) {
        return new RetType<>() {
            @Override
            public MutableText asText() {
                return Text.translatableWithFallback("trickster.fragment.class." + type.getSimpleName(), type.getSimpleName()).withColor(0xaa4444);
            }

            @Override
            public EvaluationResult into(C result) {
                return result;
            }
        };
    }
}

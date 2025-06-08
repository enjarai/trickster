package dev.enjarai.trickster.spell.type;

import java.util.Optional;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface RetType<T> {
    RetType<Fragment> ANY = simple(Fragment.class);

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
                return result.map(RetType.this::into).orElse(VoidFragment.INSTANCE);
            }
        };
    }

    default RetType<ListFragment> listOf() {
        return new RetType<>() {
            @Override
            public MutableText asText() {
                return Text.literal("[").append(RetType.this.asText()).append("]");
            }

            @Override
            public EvaluationResult into(ListFragment result) {
                return result;
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

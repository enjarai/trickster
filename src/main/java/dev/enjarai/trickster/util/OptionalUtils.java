package dev.enjarai.trickster.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class OptionalUtils {
    public static <A, B, C> Optional<C> lift2(BiFunction<A, B, C> op, Optional<A> left, Optional<B> right) {
        if (left.isPresent() && right.isPresent())
            return Optional.of(op.apply(left.get(), right.get()));

        return Optional.empty();
    }

    public static <T> Optional<T> conditional(Predicate<T> predicate, T value) {
        return predicate.test(value) ? Optional.of(value) : Optional.empty();
    }
}
